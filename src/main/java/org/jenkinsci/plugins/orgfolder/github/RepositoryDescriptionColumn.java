package org.jenkinsci.plugins.orgfolder.github;

import hudson.views.ListViewColumn;
import java.io.ObjectStreamException;
import org.jenkinsci.plugins.github_branch_source.GitHubRepositoryDescriptionColumn;

/**
 * {@link ListViewColumn} that shows the description text of repository.
 *
 * @author Kohsuke Kawaguchi
 * @deprecated use {@link GitHubRepositoryDescriptionColumn}
 */
@Deprecated
public class RepositoryDescriptionColumn extends GitHubRepositoryDescriptionColumn {
    private Object readResolve() throws ObjectStreamException {
        return new GitHubRepositoryDescriptionColumn();
    }
}
