def call(body) {
    body()
    pipeline {
        agent any
        triggers { 
            //cron( env.BRANCH_NAME == 'main' && (env.GIT_URL.contains('infra') || env.GIT_URL.contains('infrastructure')) ? '0 1 * * 1' : '')
            cron( (env.GIT_URL.contains('infra') || env.GIT_URL.contains('infrastructure')) ? 'H 1 * * 1' : '')
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
