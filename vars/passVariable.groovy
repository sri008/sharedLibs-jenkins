def call(body) {
    def config =[:]
	body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    def scanTypeChoices = []
    if (!config.infra) {
        scanTypeChoices = ['Auto', 'Manual']
    }
    // config.repoName = env.GIT_URL.replaceAll("https://github.com","").replaceAll(".git", "")
    pipeline {
        agent any
        triggers { 
            cron((env.BRANCH_NAME == 'main' && !config.infra ) ? 'H 1 * * 1': '')
        }
        stages {
            stage('github url') {
                steps { 
		    script {
			    config.gitRepo = env.GIT_URL.replaceAll(/(https:\/\/github.com\/|\.git)/, "")
			     childVar.childOne(config)
			    // config.gitRepo = env.GIT_URL.replaceAll(/(https:\/\/github.com\/|\.git)/, "")
			    // config.lastWord = config.gitRepo.contains("-cron-jobs")? "crons": config.gitRepo.endsWith("-new-notification") ? "wow" : config.gitRepo.split("[^\\w]+").last()
			    // if (config.repoName.contains("cron-jobs")) {
				   //  config.lastword = 'crons'
			    // } else {
				   //  config.lastword = config.repoName.split("[^\\w]+").last()
			    // }
		    }
                    echo "Git url --> ${env.GIT_URL}"
		    // echo "repo Name --> ${config.repoName}"
		    // echo "last word --> ${config.lastWord}"
                }
            }
        }
    }
}
