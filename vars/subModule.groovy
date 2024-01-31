def call(body) {
    // pipeline {
    //     agent any
    //     stages {
    //         stage('parent') {
    //             steps {
    //                 sh 'echo Heloo from child'
    //                 sh 'ls -la'
    //                 sh 'cat .git/config'
    //                 sh 'cat .gitmodules'
    //             }
    //         }
    //         stage('initialize sub module'){
    //             steps{
    //                 // sh 'ls -l'
    //                 // sh 'git submodule init'
    //                 // sh 'git submodule update'
    //                 // sh 'ls -l'
    //                 script {
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
                            git submodule sync
                            git submodule update --init --recursive --remote
                            cat .git/config
                            cd ${submodulePath.trim()}
                            git branch
                            git checkout -b env.GIT_BRANCH
                            cp ../*.tgz .
                            git status
                            git add . ; git commit -m "fix patch" ; git push 
                            
                            curl -X POST -u "sri008:Sri811kri$" -d '{"title": "Test automatic PR creation ", "head": "${gitB_name}-01", "base": "${baseBranch}", "body": ""}' https://api.github.com/repos/sri008/test-cron-jobs/pulls
                            
                            """
                        }
//                     }
//                 }
//             }
//         }
//         post {
//             always {
//                 cleanWs()
//             }
//         }
//     }
}