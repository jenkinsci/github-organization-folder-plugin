package org.jenkinsci.plugins.orgfolder.github;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.ObjectStreamException;
import jenkins.branch.MetadataActionFolderIcon;

/**
 * Shows Avatar icon from GitHub organization/user.
 *
 * @author Kohsuke Kawaguchi
 * @deprecated use {@link MetadataActionFolderIcon}
 */
@Deprecated
@SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_SUPERCLASS")
public class GitHubOrgIcon extends MetadataActionFolderIcon {
    private Object readResolve() throws ObjectStreamException {
        return new MetadataActionFolderIcon();
    }
}
