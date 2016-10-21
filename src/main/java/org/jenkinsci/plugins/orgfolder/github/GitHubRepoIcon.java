package org.jenkinsci.plugins.orgfolder.github;

import com.cloudbees.hudson.plugins.folder.FolderIcon;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.ObjectStreamException;

/**
 * {@link FolderIcon} that shows the github repository icon.
 *
 * @author Kohsuke Kawaguchi
 * @deprecated use {@link org.jenkinsci.plugins.github_branch_source.GitHubRepoIcon}
 */
@Deprecated
@SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_SUPERCLASS")
public class GitHubRepoIcon extends org.jenkinsci.plugins.github_branch_source.GitHubRepoIcon {

    private Object readResolve() throws ObjectStreamException {
        return new org.jenkinsci.plugins.github_branch_source.GitHubRepoIcon();
    }
}
