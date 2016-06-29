package org.jenkinsci.plugins.orgfolder.github;

import com.cloudbees.plugins.credentials.common.StandardCredentials;
import hudson.BulkChange;
import hudson.Extension;
import hudson.model.AbstractItem;
import hudson.model.AllView;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.model.ListView;
import hudson.model.listeners.ItemListener;
import hudson.util.DescribableList;
import hudson.views.JobColumn;
import hudson.views.ListViewColumn;
import hudson.views.StatusColumn;
import hudson.views.WeatherColumn;
import jenkins.branch.Branch;
import jenkins.branch.OrganizationFolder;
import jenkins.model.Jenkins;
import jenkins.scm.api.SCMSourceOwner;
import jenkins.util.io.FileBoolean;
import org.jenkinsci.plugins.github_branch_source.Connector;
import org.jenkinsci.plugins.github_branch_source.GitHubSCMNavigator;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject;
import org.kohsuke.github.GHEvent;
import org.kohsuke.github.GHHook;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Arrays.*;
import jenkins.scm.api.actions.ChangeRequestAction;
import org.jenkinsci.plugins.github_branch_source.RateLimitExceededException;

/**
 * Main logic of UI customization.
 *
 * @author Kohsuke Kawaguchi
 */
@Extension
public class MainLogic {
    /**
     * Applies UI customizations to {@link OrganizationFolder} for GitHub
     */
    public void applyOrg(OrganizationFolder of, GitHubSCMNavigator scm) throws IOException {
        if (UPDATING.get().add(of)) {
            BulkChange bc = new BulkChange(of);
            try {
                GitHub hub = connect(of, scm);
                GHUser u = hub.getUser(scm.getRepoOwner());

                of.setIcon(new GitHubOrgIcon());
                of.replaceAction(new GitHubOrgAction(u));
                of.replaceAction(new GitHubLink("logo",u.getHtmlUrl()));
                if (of.getDisplayNameOrNull()==null)
                    of.setDisplayName(u.getName());
                if (of.getView("Repositories")==null && of.getView("All") instanceof AllView) {
                    // need to set the default view
                    ListView lv = new ListView("Repositories");
                    lv.getColumns().replaceBy(asList(
                        new StatusColumn(),
                        new WeatherColumn(),
                        new CustomNameJobColumn(Messages.class,Messages._ListViewColumn_Repository()),
                        new RepositoryDescriptionColumn()
                    ));
                    lv.setIncludeRegex(".*");   // show all
                    of.addView(lv);
                    of.deleteView(of.getView("All"));
                    of.setPrimaryView(lv);
                }

                FileBoolean orghook = new FileBoolean(new File(of.getRootDir(),"GitHubOrgHook."+scm.getRepoOwner()));
                if (orghook.isOff()) {
                    try {
                        GHOrganization org = hub.getOrganization(scm.getRepoOwner());
                        String url = Jenkins.getActiveInstance().getRootUrl() + "github-webhook/";
                        if (!existsHook(org, url)) {
                            org.createWebHook(new URL(url), Arrays.asList(GHEvent.REPOSITORY,GHEvent.PUSH));
                            LOGGER.log(Level.INFO, "A webhook was registered for the organization {0}", org.getHtmlUrl());
                        }
                        // keep trying until the hook gets successfully installed
                        // if the user doesn't have the proper permission, this will cause
                        // a repeated failure, but this code doesn't execute too often.
                        orghook.on();
                    } catch (FileNotFoundException e) {
                        LOGGER.log(Level.WARNING, "Failed to register GitHub Org hook to {0} (missing permissions?): {1}", new Object[] {u.getHtmlUrl(), e.getMessage()});
                        LOGGER.log(Level.FINE, null, e);
                    } catch (RateLimitExceededException e) {
                        LOGGER.log(Level.WARNING, "Failed to register GitHub Org hook to {0}: {1}", new Object[] {u.getHtmlUrl(), e.getMessage()});
                        LOGGER.log(Level.FINE, null, e);
                    } catch (IOException e) {
                        LOGGER.log(Level.WARNING, "Failed to register GitHub Org hook to "+u.getHtmlUrl(), e);
                    }
                }


                bc.commit();
            } finally {
                bc.abort();
                UPDATING.get().remove(of);
            }
        }
    }

    /**
     * Applies UI customizations to a level below {@link OrganizationFolder}, which maps to a repository.
     */
    public void applyRepo(WorkflowMultiBranchProject item, GitHubSCMNavigator scm) throws IOException {
        if (UPDATING.get().add(item)) {
            BulkChange bc = new BulkChange(item);
            try {
                GitHub hub = connect(item, scm);
                GHRepository repo = hub.getRepository(scm.getRepoOwner() + '/' + item.getName());

                item.setIcon(new GitHubRepoIcon());
                item.replaceAction(new GitHubRepoAction(repo));
                item.replaceAction(new GitHubLink("repo",repo.getHtmlUrl()));
                if (item.getView("Branches")==null && item.getView("All") instanceof AllView) {
                    // create initial views
                    ListView bv = new ListView("Branches");
                    DescribableList<ListViewColumn, Descriptor<ListViewColumn>> cols = bv.getColumns();
                    JobColumn name = cols.get(JobColumn.class);
                    if (name!=null)
                        cols.replace(name,new CustomNameJobColumn(Messages.class, Messages._ListViewColumn_Branch()));
                    bv.getJobFilters().add(new BranchJobFilter());

                    ListView pv = new ListView("Pull Requests");
                    cols = pv.getColumns();
                    name = cols.get(JobColumn.class);
                    if (name!=null)
                        cols.replace(name,new CustomNameJobColumn(Messages.class, Messages._ListViewColumn_PullRequest()));
                    pv.getJobFilters().add(new PullRequestJobFilter());

                    item.addView(bv);
                    item.addView(pv);
                    item.deleteView(item.getView("All"));
                    item.setPrimaryView(bv);
                }

                bc.commit();
            } finally {
                bc.abort();
                UPDATING.get().remove(item);
            }
        }
    }

    /**
     * Applies UI customizations to two levels below {@link OrganizationFolder}, which maps to a branch.
     */
    public void applyBranch(WorkflowJob branch, WorkflowMultiBranchProject repo, GitHubSCMNavigator scm) throws IOException {
        if (UPDATING.get().add(branch)) {
            BulkChange bc = new BulkChange(branch);
            try {
                Branch b = repo.getProjectFactory().getBranch(branch);
                GitHubLink repoLink = repo.getAction(GitHubLink.class);
                if (repoLink!=null) {
                    ChangeRequestAction action = b.getHead().getAction(ChangeRequestAction.class);
                    String url;
                    if (action == null) {
                        // branch in this repository
                        url = repoLink.getUrl() + "/tree/" + b.getName();
                    } else {
                        // pull request to this repository
                        url = repoLink.getUrl() + "/pull/" + action.getId();
                    }
                    branch.replaceAction(new GitHubLink("branch", url));
                    bc.commit();
                }
            } finally {
                bc.abort();
                UPDATING.get().remove(branch);
            }
        }
    }

    GitHub connect(SCMSourceOwner of, GitHubSCMNavigator n) throws IOException {
        StandardCredentials credentials = Connector.lookupScanCredentials(of, n.getApiUri(), n.getScanCredentialsId());
        return Connector.connect(n.getApiUri(), credentials);
    }

    /**
     * Verify if exists a webhook by its URL.
     */
    private boolean existsHook(GHOrganization org, String url) throws IOException {
        for (GHHook hook : org.getHooks()) {
            if (hook.getConfig().get("url").equals(url)) {
                return true;
            }
        }
        return false;
    }

    public static MainLogic get() {
        return Jenkins.getActiveInstance().getInjector().getInstance(MainLogic.class);
    }

    /**
     * Keeps track of what we are updating to avoid recursion, because {@link AbstractItem#save()}
     * triggers {@link ItemListener}.
     */
    private final ThreadLocal<Set<Item>> UPDATING = new ThreadLocal<Set<Item>>() {
        @Override
        protected Set<Item> initialValue() {
            return new HashSet<>();
        }
    };

    private static final Logger LOGGER = Logger.getLogger(MainLogic.class.getName());
}
