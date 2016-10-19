package org.jenkinsci.plugins.orgfolder.github;

import hudson.model.TopLevelItem;
import hudson.model.View;
import hudson.views.ViewJobFilter;
import java.io.ObjectStreamException;
import java.util.List;
import org.jenkinsci.plugins.github_branch_source.GitHubPullRequestFilter;

/**
 * Retained for on-disk compatibility only.
 * @deprecated use {@link GitHubPullRequestFilter}
 */
@Deprecated
public final class PullRequestJobFilter extends ViewJobFilter {

    private PullRequestJobFilter() {
        throw new IllegalStateException("Deprecated");
    }

    @Override
    public List<TopLevelItem> filter(List<TopLevelItem> added, List<TopLevelItem> all, View filteringView) {
        return added;
    }

    private Object readResolve() throws ObjectStreamException {
        return new GitHubPullRequestFilter();
    }

}
