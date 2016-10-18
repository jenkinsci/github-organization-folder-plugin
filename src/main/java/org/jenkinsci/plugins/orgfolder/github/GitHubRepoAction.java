package org.jenkinsci.plugins.orgfolder.github;

import hudson.model.InvisibleAction;
import java.io.ObjectStreamException;
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject;
import org.kohsuke.github.GHRepository;

import java.net.URL;

/**
 * Invisible property on {@link WorkflowMultiBranchProject}
 * that retains information about GitHub repository.
 *
 * @author Kohsuke Kawaguchi
 * @deprecated use {@link org.jenkinsci.plugins.github_branch_source.GitHubRepoAction}
 */
@Deprecated
public class GitHubRepoAction extends org.jenkinsci.plugins.github_branch_source.GitHubRepoAction {

    GitHubRepoAction(GHRepository repo) {
        super(repo);
    }

    private Object readResolve() throws ObjectStreamException {
        return new org.jenkinsci.plugins.github_branch_source.GitHubRepoAction(this);
    }


}
