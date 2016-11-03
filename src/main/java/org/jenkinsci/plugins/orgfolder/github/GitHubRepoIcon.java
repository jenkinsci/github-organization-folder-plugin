package org.jenkinsci.plugins.orgfolder.github;

import com.cloudbees.hudson.plugins.folder.FolderIcon;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.ObjectStreamException;
import jenkins.branch.MetadataActionFolderIcon;

/**
 * {@link FolderIcon} that shows the github repository icon.
 *
 * @author Kohsuke Kawaguchi
 * @deprecated use {@link MetadataActionFolderIcon}
 */
@Deprecated
@SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_SUPERCLASS")
public class GitHubRepoIcon extends MetadataActionFolderIcon {

    private Object readResolve() throws ObjectStreamException {
        return new MetadataActionFolderIcon();
    }
}
