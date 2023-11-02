def childOne(config) {
	// config.gitRepo = env.GIT_URL.replaceAll(/(https:\/\/github.com\/|\.git)/, "")
	def lastWord = config.gitRepo.contains("-cron-jobs")? "crons": config.gitRepo.endsWith("-new-notification") ? "wow" : config.gitRepo.split("[^\\w]+").last()
	withEnv(["lastword"]){
		sh """echo last word --> $lastWord"""
	}
	
}
