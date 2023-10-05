def call(body) {
    body()
    // def getCronParams() {
    //     if(env.GIT_URL.contains('infra') ) {
    //         return 'H */4 * * 1-5'
    //     } 
    //     else {
    //         return ''
    //     }
    // }
    def cronValue
    if (env.GIT_URL.contains('infra') || env.GIT_URL.contains('infrastructure')) {
        cronValue = 'H */4 * * 1-5'
    } else {
        cronValue = ''
    }
    pipeline {
        agent any
        triggers { 
            // cron( env.BRANCH_NAME == 'main' && env.GIT_URL.contains('infra') == 'true' ? '0 1 * * 1' : '')
            cron( cronValue)
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
