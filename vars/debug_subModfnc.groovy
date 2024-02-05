def call(body){
    def gitB_name = env.GIT_BRANCH
    def gitmodulesContent = readFile('.gitmodules')
    def lines = gitmodulesContent.readLines()
    def submoduleRegex = /\[submodule "(.*?)"]/
    def submodules = []

    lines.eachWithIndex { line, index ->
        def submoduleMatcher = (line =~ submoduleRegex)
        if (submoduleMatcher) {
            def submodule = [:]
            submodule['path'] = submoduleMatcher[0][1]
            submodule['url'] = lines[index + 2].trim().split("=")[1].trim()
            submodules.add(submodule)
        }
    }

    submodules.each { submodule ->
        echo "Path: ${submodule['path']}"
        echo "URL: ${submodule['url']}"
        echo '---'
         withCredentials([string(credentialsId: 'testAPi', variable: 'github_token')]) {
            sh """ 
                cd ${submodule['path']}
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
                curl -X POST -H "Accept: application/vnd.github+json" -H "Authorization: Bearer ${github_token}" -H "X-GitHub-Api-Version: 2022-11-28" \
                    -d '{"title": "Test automatic PR creation ", "head": "${gitB_name}", "base": "main", "body": ""}' \
                    ${submodule['url']}/pulls
            """
        }
    }
}