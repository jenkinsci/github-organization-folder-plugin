package org.jenkinsci.plugins.orgfolder.github;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.ObjectStreamException;

/**
 * Shows Avatar icon from GitHub organization/user.
 *
 * @author Kohsuke Kawaguchi
 * @deprecated use {@link org.jenkinsci.plugins.github_branch_source.GitHubOrgIcon}
 */
@Deprecated
@SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_SUPERCLASS")
public class GitHubOrgIcon extends org.jenkinsci.plugins.github_branch_source.GitHubOrgIcon {
    private Object readResolve() throws ObjectStreamException {
        return new org.jenkinsci.plugins.github_branch_source.GitHubOrgIcon();
    }
}
