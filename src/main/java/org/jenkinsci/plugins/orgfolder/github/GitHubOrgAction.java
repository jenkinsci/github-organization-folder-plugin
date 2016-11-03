package org.jenkinsci.plugins.orgfolder.github;

import java.io.ObjectStreamException;
import jenkins.branch.OrganizationFolder;
import org.jenkinsci.plugins.github_branch_source.GitHubOrgMetadataAction;
import org.kohsuke.github.GHUser;

import java.io.IOException;

/**
 * Invisible {@link OrganizationFolder} property that
 * retains information about GitHub organization.
 *
 * @author Kohsuke Kawaguchi
 * @deprecated use {@link GitHubOrgMetadataAction}
 */
@Deprecated
public class GitHubOrgAction extends GitHubOrgMetadataAction {

    GitHubOrgAction(GHUser org) throws IOException {
        super(org);
    }

    private Object readResolve() throws ObjectStreamException{
        return new GitHubOrgMetadataAction(this);
    }

}
