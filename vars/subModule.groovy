def parentPipeline(body) {
    pipeline {
        agent any
        stages {
            stage('parent') {
                steps {
                    sh 'echo Heloo from child'
                    sh 'ls -la'
                    sh 'cat .git/config'
                    sh 'cat .gitmodules'
                }
            }
            stage('initialize sub module'){
                steps{
                    // sh 'ls -l'
                    // sh 'git submodule init'
                    // sh 'git submodule update'
                    // sh 'ls -l'
                    script {
                         // Read the .gitmodules file and extract submodule paths
                        def gitmodulesContent = readFile('.gitmodules')
                        def submodulePaths = gitmodulesContent.readLines().findAll { it =~ /^\s*path\s*=/ }.collect { it.replaceFirst(/^\s*path\s*=\s*/, '') }

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
                            ls -la
                            """
                        }
                    }
                }
            }
        }
        post {
            always {
                cleanWs()
            }
        }
    }
}