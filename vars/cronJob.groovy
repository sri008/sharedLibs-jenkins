// def call(Map params = [:]) {
def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    config.each { print(it) }
    pipeline {
        agent any
        triggers { 
            // cron( env.BRANCH_NAME == 'main' && (env.GIT_URL.contains('infra') || env.GIT_URL.contains('infrastructure')) ? '0 1 * * 1' : '')
            // cron( (gitURL.contains('infra') || gitURL.contains('infrastructure')) ? 'H 1 * * 1' : '')
            cron('H 1 * * 1')
        }
        stages {
            stage('github url') {
                steps {
                    echo "${env.GIT_URL}"
                    echo "${env.BRANCH_NAME}"
                    // echo "THE Branch is use ==> ${branchName}"
                    // echo "the gitlhub repo url use ==> ${gitURL}"
                }
            }
        }
    }
}
