def call(body) {
    pipeline {
        agent any
        stages {
            stage('parent') {
                steps {
                    sh 'echo Heloo from child'
                    sh 'ls -l'
                }
            }
            stage('initialize sub module'){
                steps{
                    sh 'ls -l'
                    sh 'git submodule init'
                    sh 'git submodule update'
                }
            }
        }
    }
}