def call(body) {
    // Read the .gitmodules file and extract submodule paths
    // def gitB_name = env.GIT_BRANCH
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
            git branch | grep -q  "${env.GIT_BRANCH}"
            if [ $? -eq 0 ]; then
                git checkout  ${env.GIT_BRANCH}
            else
                git checkout -b  ${env.GIT_BRANCH}
            fi
        """
    }
}