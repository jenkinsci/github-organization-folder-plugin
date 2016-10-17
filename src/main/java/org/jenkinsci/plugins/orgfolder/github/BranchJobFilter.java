package org.jenkinsci.plugins.orgfolder.github;

import hudson.model.TopLevelItem;
import hudson.model.View;
import hudson.views.ViewJobFilter;
import java.io.ObjectStreamException;
import java.util.List;
import jenkins.scm.api.SCMHead;
import org.jenkinsci.plugins.github_branch_source.GitHubBranchFilter;

/**
 * Retained for on-disk compatibility only.
 * @deprecated use {@link GitHubBranchFilter}
 */
@Deprecated
public final class BranchJobFilter extends ViewJobFilter {

    private BranchJobFilter() {
        throw new IllegalStateException("Deprecated");
    }

    @Override
    public List<TopLevelItem> filter(List<TopLevelItem> added, List<TopLevelItem> all, View filteringView) {
        return added;
    }

    private Object readResolve() throws ObjectStreamException {
        return new GitHubBranchFilter();
    }

}
