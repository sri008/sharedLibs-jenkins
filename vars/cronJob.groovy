def call(body) {
    def config =[:]
	body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    pipeline {
        agent any
        triggers { 
            // cron( (env.BRANCH_NAME == 'main' && (env.GIT_URL.contains('infra') || env.GIT_URL.contains('infrastructure'))) ? 'H 1 * * 1' : '')
            // cron( (gitURL.contains('infra') || gitURL.contains('infrastructure')) ? 'H 1 * * 1' : '')
            cron((env.BRANCH_NAME == 'main' && config.infra == 'true' ) ? 'H 1 * * 1': '')
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
