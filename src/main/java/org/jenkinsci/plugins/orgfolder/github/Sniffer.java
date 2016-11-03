package org.jenkinsci.plugins.orgfolder.github;

import hudson.model.Item;
import hudson.util.DescribableList;
import java.util.List;
import jenkins.branch.Branch;
import jenkins.branch.OrganizationFolder;
import jenkins.scm.api.SCMNavigator;
import jenkins.scm.api.SCMNavigatorDescriptor;
import org.jenkinsci.plugins.github_branch_source.GitHubSCMNavigator;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject;

/**
 * Pattern matching on GitHub organization folder member {@link Item}s.
 *
 * @author Kohsuke Kawaguchi
 */
class Sniffer {
    static class OrgMatch {
        final OrganizationFolder folder;
        final GitHubSCMNavigator scm;

        public OrgMatch(OrganizationFolder folder, GitHubSCMNavigator scm) {
            this.folder = folder;
            this.scm = scm;
        }
    }

    public static OrgMatch matchOrg(Object item) {
        if (item instanceof OrganizationFolder) {
            OrganizationFolder of = (OrganizationFolder)item;
            List<SCMNavigator> navigators = of.getNavigators();
            if (/* could be called from constructor */navigators != null && navigators.size() > 0) {
                SCMNavigator n = navigators.get(0);
                if (n instanceof GitHubSCMNavigator) {
                    return new OrgMatch(of, (GitHubSCMNavigator) n);
                }
            }
        }
        return null;
    }

    static class RepoMatch extends OrgMatch {
        final WorkflowMultiBranchProject repo;

        public RepoMatch(OrgMatch x, WorkflowMultiBranchProject repo) {
            super(x.folder,x.scm);
            this.repo = repo;
        }
    }

    public static RepoMatch matchRepo(Object item) {
        if (item instanceof WorkflowMultiBranchProject) {
            WorkflowMultiBranchProject repo = (WorkflowMultiBranchProject)item;
            OrgMatch org = matchOrg(repo.getParent());
            if (org!=null)
                return new RepoMatch(org, repo);
        }
        return null;
    }

}
