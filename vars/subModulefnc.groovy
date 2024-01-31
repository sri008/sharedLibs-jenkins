def call(body) {
    // Read the .gitmodules file and extract submodule paths
    def gitB_name = env.GIT_BRANCH
    def gitmodulesContent = readFile('.gitmodules')
    def submodulePaths = gitmodulesContent.readLines().findAll { it =~ /^\s*path\s*=/ }.collect { it.replaceFirst(/^\s*path\s*=\s*/, '') }
    def baseBranch = gitmodulesContent.readLines().find { it =~ /branch\s*=/ }?.replaceAll(/^\s*branch\s*=\s*/, '') ?: 'main'

    // Iterate through each submodule path
    submodulePaths.each { submodulePath ->
        sh "ls -l ${submodulePath.trim()}"
        sh """
            git config --list
            echo ##########
            git submodule sync ; git submodule update --init --recursive --remote
            cat .git/config
            cd ${submodulePath.trim()}
            ls -l 
            if [[ \$(git branch | grep -q "${gitB_name}") ]]; then
                git checkout "$gitB_name"
            else
                git checkout -b "$gitB_name"
            fi
            git config --global user.email "srikant_008@live.com"
            git config --global user.name "sri008" 
            cp ../*.tgz .
			git status
			git add . ; git commit -m "fix patch" ; git push --set-upstream origin "$gitB_name"; git push
            curl -X POST -H "Accept: application/vnd.github+json" -H "Authorization: Bearer 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" -H "X-GitHub-Api-Version: 2022-11-28" \
                -d '{"title": "Test automatic PR creation ", "head": "${gitB_name}-01", "base": "${baseBranch}", "body": ""}' \
                https://api.github.com/repos/sri008/test-cron-jobs/pulls
        """
    }
}