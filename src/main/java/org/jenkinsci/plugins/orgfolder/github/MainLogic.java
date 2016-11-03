package org.jenkinsci.plugins.orgfolder.github;

import com.cloudbees.plugins.credentials.common.StandardCredentials;
import hudson.BulkChange;
import hudson.Extension;
import hudson.model.AbstractItem;
import hudson.model.Item;
import hudson.model.listeners.ItemListener;
import jenkins.branch.OrganizationFolder;
import jenkins.model.Jenkins;
import jenkins.scm.api.SCMSourceOwner;
import jenkins.util.io.FileBoolean;
import org.jenkinsci.plugins.github_branch_source.Connector;
import org.jenkinsci.plugins.github_branch_source.GitHubSCMNavigator;
import org.kohsuke.github.GHEvent;
import org.kohsuke.github.GHHook;
import org.kohsuke.github.GHOrganization;
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
