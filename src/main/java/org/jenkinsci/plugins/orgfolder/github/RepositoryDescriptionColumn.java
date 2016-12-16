package org.jenkinsci.plugins.orgfolder.github;

import hudson.views.ListViewColumn;
import java.io.ObjectStreamException;
import jenkins.branch.DescriptionColumn;

/**
 * {@link ListViewColumn} that shows the description text of repository.
 *
 * @author Kohsuke Kawaguchi
 * @deprecated use {@link DescriptionColumn}
 */
@Deprecated
public class RepositoryDescriptionColumn extends DescriptionColumn {
    private Object readResolve() throws ObjectStreamException {
        return new DescriptionColumn();
    }
}
