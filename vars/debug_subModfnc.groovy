def call(body){
    def gitB_name = env.GIT_BRANCH
    def gitmodulesContent = readFile('.gitmodules')
    // Define a regular expression to match submodule entries
    def regex = /\[submodule "(.*?)"]\n\s*path = (.*?)\n\s*url = (.*?)\n/
    // Use a matcher to find all submodule entries
    def matcher = (gitmodulesContent =~ regex)

    sh "git submodule sync ; git submodule update --init --recursive --remote"
    // Iterate through matches and print path and url values
    matcher.each { match ->
        def submodule = [:]
        submodule['path'] = match[2]
        submodule['url'] = match[3]

        println "Path: ${submodule['path']}"
        println "URL: ${submodule['url']}"
        println '---'
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