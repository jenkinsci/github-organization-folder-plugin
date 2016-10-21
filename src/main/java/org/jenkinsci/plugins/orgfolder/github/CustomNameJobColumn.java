package org.jenkinsci.plugins.orgfolder.github;

import hudson.views.JobColumn;

/**
 * {@link JobColumn} with different caption
 *
 * @author Kohsuke Kawaguchi
 * @deprecated use {@link JobColumn}
 */
@Deprecated
public class CustomNameJobColumn extends JobColumn {

    private Object readResolve() {
        return new JobColumn();
    }

}
