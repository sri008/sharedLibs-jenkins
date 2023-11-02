def childOne(config) {
	// config.gitRepo = env.GIT_URL.replaceAll(/(https:\/\/github.com\/|\.git)/, "")
	def lastWord = config.gitRepo.contains("-cron-jobs")? "crons": config.gitRepo.endsWith("-new-notification") ? "wow" : config.gitRepo.split("[^\\w]+").last()
	echo "Debug: last word --> ${lastWord}"
	withEnv(["LASTWORD=${lastWord}"]){
		sh """echo last word ecr-$LASTWORD-image
                    echo ${config.gitRepo}"""
	}
}
