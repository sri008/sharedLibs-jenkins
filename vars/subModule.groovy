def call(body) {
    pipeline {
        agent any
        stages {
            stage('clear workspace') {
                steps {
                    cleanWs()
                }
            }
            stage('parent') {
                steps {
                    sh 'echo Heloo from child'
                    sh 'ls -l'
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

                        // Initialize and update each submodule
                        submodulePaths.each { submodulePath ->
                            dir(submodulePath.trim()) {
                                sh 'git submodule init'
                                sh 'git submodule update'
                            }
                        }
                    }
                }
            }
        }
    }
}