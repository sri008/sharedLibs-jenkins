def call(body) {
    def config =[:]
	body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    properties([
        parameters([
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
            ],
            [
              $class: 'ChoiceParameter',
              choiceType: 'PT_SINGLE_SELECT',
              name: 'ScanType',
              script: [
                $class: 'GroovyScript',
                script: [
                    classpath: [],
                    script:
                        '''if(config.buildDocker = 'true') {
                            return[\'Auto\',\'Manual\']   
                        } else {
                            return["Infra repo"]
                        }'''
                    ]
              ]
            ],
            [
                $class: 'CascadeChoiceParameter',
                 choiceType: 'PT_SINGLE_SELECT',
                name: 'Region',
                referencedParameters: 'ScanType',
                script: [
                    $class: 'GroovyScript',
                    script: 
                        [classpath: [], 
                        script:
                            '''if (ScanType.equals("Auto")) {
                              return ["ap-south-1"]
                            }'''
                        ]
                ]
            ]
        ])
    ])

    pipeline {
        agent any
        triggers { 
            cron((env.BRANCH_NAME == 'main' && !config.infra ) ? 'H 1 * * 1': '')
        }
        stages {
            stage('github url') {
                steps {
                    echo "${env.GIT_URL}"
                    echo "${env.BRANCH_NAME}"
                    echo "${config.infra}"
            }
        }
    }
}
