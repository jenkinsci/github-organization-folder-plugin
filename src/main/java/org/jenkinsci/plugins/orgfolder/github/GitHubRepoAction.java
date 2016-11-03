package org.jenkinsci.plugins.orgfolder.github;

import java.io.ObjectStreamException;
import org.jenkinsci.plugins.github_branch_source.GitHubRepoMetadataAction;
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject;
import org.kohsuke.github.GHRepository;

/**
 * Invisible property on {@link WorkflowMultiBranchProject}
 * that retains information about GitHub repository.
 *
 * @author Kohsuke Kawaguchi
 * @deprecated use {@link GitHubRepoMetadataAction}
 */
@Deprecated
public class GitHubRepoAction extends GitHubRepoMetadataAction {

    GitHubRepoAction(GHRepository repo) {
        super(repo);
    }

    private Object readResolve() throws ObjectStreamException {
        return new GitHubRepoMetadataAction(this);
    }


}
