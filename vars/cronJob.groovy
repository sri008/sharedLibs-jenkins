def call(body) {
    body()
    // cant use thi below method as it should be declare in another file as body() method is used
    // def getCronParams() {
    //     if(env.GIT_URL.contains('infra') ) {
    //         return 'H */4 * * 1-5'
    //     } 
    //     else {
    //         return ''
    //     }
    // }
    
    pipeline {
        agent any
        triggers { 
            cron( env.BRANCH_NAME == 'main' && (env.GIT_URL.contains('infra') || env.GIT_URL.contains('infrastructure')) ? '0 1 * * 1' : '')
            // cron( cronValue)
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
