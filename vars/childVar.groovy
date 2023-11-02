def childOne(config) {
	// config.gitRepo = env.GIT_URL.replaceAll(/(https:\/\/github.com\/|\.git)/, "")
	def lastWord = config.gitRepo.contains("-cron-jobs")? "crons": config.gitRepo.endsWith("-new-notification") ? "wow" : config.gitRepo.split("[^\\w]+").last()
	echo "Debug: last word --> ${lastWord}"
	echo "Git url from main shared file--> ${env.GIT_URL}"
	withEnv(["LASTWORD=${lastWord}"]){
		sh """echo last word ecr-$LASTWORD-image
                    echo ${config.gitRepo}"""
	}
}
