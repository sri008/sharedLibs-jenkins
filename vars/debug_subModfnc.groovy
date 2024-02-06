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
            // when the url in https format then use only below line
            // submodule['url'] = lines[index + 2].trim().split("=")[1].trim()
            // Extracting the URL from the line and trimming whitespace
            def url = lines[index + 2].trim().split("=")[1].trim()

            // Converting SSH URL to GitHub API URL for creating pull requests
            if (!url.startsWith("https://api.github.com/repos")) {
                def parts = url.split(':')
                def repoParts = parts[1].split('/')
                def owner = repoParts[0]
                def repo = repoParts[1].substring(0, repoParts[1].lastIndexOf('.git'))

                url = "https://api.github.com/repos/${owner}/${repo}/pulls"
            }

            submodule['url'] = url

             // Extracting the branch from the line
            // Find the branch line for the current submodule
            def branchLine = lines.find { it.startsWith("branch =") && it.contains(submodule['path']) }
            def branch = branchLine ? branchLine.split("=")[1].trim() : 'main'
            submodule['branch'] = branch

            submodules.add(submodule)
        }
    }

    sh "git submodule sync ; git submodule update --init --recursive --remote"
    submodules.each { submodule ->
        echo "Path: ${submodule['path']}"
        echo "URL: ${submodule['url']}"
        echo "Bbranch: ${submodule['branch']}"
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
                    -d '{"title": "Test automatic PR creation ", "head": "${gitB_name}", "base": "${submodule['branch']}", "body": ""}' \
                    ${submodule['url']}
            """
        }
    }
}