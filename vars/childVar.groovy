def childOne(config) {
	// config.gitRepo = env.GIT_URL.replaceAll(/(https:\/\/github.com\/|\.git)/, "")
	withEnv(["def lastWord = config.gitRepo.contains("-cron-jobs")? "crons": config.gitRepo.endsWith("-new-notification") ? "wow" : config.gitRepo.split("[^\\w]+").last()"]){
		sh """echo last word --> $lastWord"""
	}
	
}
