def call(body) {
    body()
    pipeline {
        agent any
        triggers { 
            // cron( env.BRANCH_NAME == 'main' && env.GIT_URL.contains('infra') == 'true' ? '0 1 * * 1' : '')
            script {
                if (env.BRANCH_NAME == 'main' && env.GIT_URL.contains('infra')) {
                    cron('0 1 * * 1')
                }
            }
        }
        stages {
            stage('github url') {
                steps {
                    echo "THE Branch is use ==> ${env.BRANCH_NAME}"
                    echo "the gitlhub repo url use ==> ${env.GIT_URL}"
                }
            }
        }
    }
}
