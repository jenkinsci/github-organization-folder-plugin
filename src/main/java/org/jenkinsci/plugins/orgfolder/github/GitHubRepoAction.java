package org.jenkinsci.plugins.orgfolder.github;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
@SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_SUPERCLASS")
public class GitHubRepoAction extends org.jenkinsci.plugins.github_branch_source.GitHubRepoAction {

    GitHubRepoAction(GHRepository repo) {
        super(repo);
    }

    private Object readResolve() throws ObjectStreamException {
        return new org.jenkinsci.plugins.github_branch_source.GitHubRepoAction(this);
    }


}
