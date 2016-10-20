package org.jenkinsci.plugins.orgfolder.github;

import hudson.Extension;
import hudson.views.JobColumn;
import hudson.views.ListViewColumnDescriptor;
import jenkins.model.Jenkins;
import jenkins.util.NonLocalizable;
import org.jvnet.localizer.Localizable;
import org.jvnet.localizer.ResourceBundleHolder;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {@link JobColumn} with different caption
 *
 * @author Kohsuke Kawaguchi
 * @deprecated use {@link com.cloudbees.hudson.plugins.folder.views.CustomNameJobColumn}
 */
@Deprecated
public class CustomNameJobColumn extends com.cloudbees.hudson.plugins.folder.views.CustomNameJobColumn {

    private CustomNameJobColumn(String bundle, String key) {
        super(bundle, key);
    }

    private Object readResolve() {
        return new com.cloudbees.hudson.plugins.folder.views.CustomNameJobColumn(getBundle(), getKey());
    }

}
