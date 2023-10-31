def call(body) {
    def config =[:]
	body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    def scanTypeChoices = []
    if (!config.infra) {
        scanTypeChoices = ['Auto', 'Manual']
    }
    pipeline {
        agent any
        
        triggers { 
            cron((env.BRANCH_NAME == 'main' && !config.infra ) ? 'H 1 * * 1': '')
        }
        stages {
            stage('set parameter') {
                steps{
                    script{
                        if (config.infra) {
                            env.INFRA_ENV = 'true'
                        } else {
                            env.INFRA_ENV = 'false'
                        }
                        def inputParams=[
                        [
                            $class: 'ChoiceParameter',
                            choiceType: 'PT_SINGLE_SELECT',
                            filterLength: 0, filterable: false,
                            name: 'ProjectName',
                            script: [
                                $class: 'GroovyScript',
                                script: 
                                    [
                                        classpath: [],  
                                        script:
                                            'return[\'SM\']'
                                    ]
                            ]
                        ]
                    ]

                    properties([
                            parameters(inputParams)
                        ])
                    }
                    
                }
            }
            stage('github url') {
                steps { 
		    script {
			    config.repoName = env.GIT_URL.replaceAll("https://github.com","").replaceAll(".git", "")
			    if (config.repoName.contains("cron-jobs")) {
				    config.lastword = 'crons'
			    } else {
				    config.lastword = config.repoName.split("[^\\w]+").last()
			    }
		    }
                    echo "Git url --> ${env.GIT_URL}"
		    echo "repo Name --> ${config.repoName}"
		    echo "last word --> ${config.lastword}"
                    echo "Branch is used --> ${env.BRANCH_NAME}"
                    echo "is it infra or not --> ${env.INFRA_ENV }"
                }
            }
        }
    }
}
