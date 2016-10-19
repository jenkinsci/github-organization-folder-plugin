package org.jenkinsci.plugins.orgfolder.github;

import java.io.ObjectStreamException;

/**
 * Shows Avatar icon from GitHub organization/user.
 *
 * @author Kohsuke Kawaguchi
 * @deprecated use {@link org.jenkinsci.plugins.github_branch_source.GitHubOrgIcon}
 */
@Deprecated
public class GitHubOrgIcon extends org.jenkinsci.plugins.github_branch_source.GitHubOrgIcon {
    private Object readResolve() throws ObjectStreamException {
        return new org.jenkinsci.plugins.github_branch_source.GitHubOrgIcon();
    }
}
