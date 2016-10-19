package org.jenkinsci.plugins.orgfolder.github;

import hudson.model.InvisibleAction;
import java.io.ObjectStreamException;
import jenkins.branch.OrganizationFolder;
import org.kohsuke.github.GHUser;

import java.io.IOException;
import java.net.URL;

/**
 * Invisible {@link OrganizationFolder} property that
 * retains information about GitHub organization.
 *
 * @author Kohsuke Kawaguchi
 * @deprecated use {@link org.jenkinsci.plugins.github_branch_source.GitHubOrgAction}
 */
@Deprecated
public class GitHubOrgAction extends org.jenkinsci.plugins.github_branch_source.GitHubOrgAction {

    GitHubOrgAction(GHUser org) throws IOException {
        super(org);
    }

    private Object readResolve() throws ObjectStreamException{
        return new org.jenkinsci.plugins.github_branch_source.GitHubOrgAction(this);
    }

}
