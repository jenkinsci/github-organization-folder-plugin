package org.jenkinsci.plugins.orgfolder.github;

import com.cloudbees.hudson.plugins.folder.AbstractFolder;
import com.cloudbees.hudson.plugins.folder.FolderIcon;
import com.cloudbees.hudson.plugins.folder.FolderIconDescriptor;
import hudson.Extension;
import hudson.model.Hudson;
import org.jenkinsci.plugins.github_branch_source.GitHubOrgAction;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.Stapler;

/**
 * Shows Avatar icon from GitHub organization/user.
 *
 * @author Kohsuke Kawaguchi
 */
public class GitHubOrgIcon extends FolderIcon {
    private AbstractFolder<?> folder;

    @DataBoundConstructor
    public GitHubOrgIcon() {
    }

    @Override
    protected void setOwner(AbstractFolder<?> folder) {
        this.folder = folder;
    }

    @Override
    public String getImageOf(String s) {
        String url = getAvatarUrl();
        if (url==null) {
            // fall back to the generic github org icon
            return Stapler.getCurrentRequest().getContextPath()+ Hudson.RESOURCE_PATH+"/plugin/github-organization-folder/images/logo/"+s+".png";
        } else {
            String[] xy = s.split("x");
            if (xy.length==0)       return url;
            if (url.contains("?"))  return url+"&s="+xy[0];
            else                    return url+"?s="+xy[0];
        }
    }

    @Override
    public String getDescription() {
        return folder!=null ? folder.getName() : "GitHub";
    }

    private String getAvatarUrl() {
        if (folder==null)   return null;
        GitHubOrgAction p = folder.getAction(GitHubOrgAction.class);
        if (p==null)    return null;
        return p.getAvatar();
    }

    @Extension
    public static class DescriptorImpl extends FolderIconDescriptor {
        @Override
        public String getDisplayName() {
            return "GitHub Organization Avatar";
        }
    }
}
