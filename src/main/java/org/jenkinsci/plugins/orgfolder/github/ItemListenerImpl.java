package org.jenkinsci.plugins.orgfolder.github;

import hudson.Extension;
import hudson.model.Item;
import hudson.model.listeners.ItemListener;
import org.jenkinsci.plugins.orgfolder.github.Sniffer.OrgMatch;
import org.jenkinsci.plugins.orgfolder.github.Sniffer.RepoMatch;

import javax.inject.Inject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hook to add decorations to GitHub organization folders.
 *
 * @author Kohsuke Kawaguchi
 */
@Extension
public class ItemListenerImpl extends ItemListener {
    @Inject
    private MainLogic main;

    @Override
    public void onUpdated(Item item) {
        maybeApply(item);
    }

    @Override
    public void onCreated(Item item) {
        maybeApply(item);
    }

    private void maybeApply(Item item) {
        try {
            OrgMatch f = Sniffer.matchOrg(item);
            if (f!=null) {
                main.applyOrg(f.folder, f.scm);
            }

            RepoMatch r = Sniffer.matchRepo(item);
            if (r!=null) {
            }

        } catch (FileNotFoundException e) {
            LOGGER.log(Level.FINE, "Failed to apply GitHub Org Folder theme to " + item.getFullName() + 
                    " because the Org does not exists or it's not accessible", e);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to apply GitHub Org Folder theme to " + item.getFullName(), e);
        }
    }

    private static final Logger LOGGER = Logger.getLogger(ItemListenerImpl.class.getName());
}
