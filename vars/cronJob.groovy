// def call(Map params = [:]) {
def call(body) {
    // def gitURL = params.GIT_URL ?: ''
    // def branchName = params.BRANCH_NAME ?: ''
    body()
    pipeline {
        agent any
        triggers { 
            //cron( env.BRANCH_NAME == 'main' && (env.GIT_URL.contains('infra') || env.GIT_URL.contains('infrastructure')) ? '0 1 * * 1' : '')
            cron( (gitURL.contains('infra') || gitURL.contains('infrastructure')) ? 'H 1 * * 1' : '')
        }
        stages {
            stage('github url') {
                steps {
                    echo "THE Branch is use ==> ${branchName}"
                    echo "the gitlhub repo url use ==> ${gitURL}"
                }
            }
        }
    }
}
