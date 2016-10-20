# GitHub Organization Folder

This plugin defines GitHub Organization Folder capability.

1. In this new subsystem, you first let Jenkins know where you host your source code repositories.
You do this by creating a new special folder called "GitHub Organization Folder" and associate
this with a GitHub organization or an user. This sets up a new smart folder.

1. In one of the repository in the chosen org, create a `Jenkinsfile` that defines your pipeline
(or just a build process to start with.) This file is actually a Pipeline script.
See [the Pipeline solution page](https://jenkins-ci.org/solutions/pipeline) for the introduction,
and [the tutorial](https://github.com/jenkinsci/workflow-plugin/blob/master/TUTORIAL.md) for more details.

1. Jenkins will automatically recognize this branch and create appropriate jobs by itself.

1. Every time a new change is pushed to this branch, the branch is built and the commit status gets updated.

1. When the branch is destroyed in the repository or if `Jenkinsfile` is removed, the corresponding job
gets destroyed from Jenkins as well automatically.

In this use, there'll be a lot of `Jenkinsfile` in various branches & repositories.
To keep them DRY, various mechanisms will be provided to promote reuse of Pipeline scripts, such as
[this](https://github.com/jenkinsci/workflow-plugin/blob/master/cps-global-lib/README.md).


# Hook configuration
For Jenkins to be able to trigger a new build whenever a new commit / pull request occurs, Jenkins
needs to receive a webhook from GitHub.

If the credentials you provided to GitHub Organization Folder allows you to install an organization hook,
this plugin will register a hook by itself. Otherwise you can also manually register a hook
to `$JENKINS_URL/github-webhook/`. 'Push', 'Pull Request' and 'Repository' event type should be selected
for the best result.



# Implementation
Much of the GitHub organization folder capability are split into three separate plugins.
Those are `branch-api`, `github-branch-source`, and `workflow-multibranch`. This plugin
pulls in all of them, then add some extra UI customization.
