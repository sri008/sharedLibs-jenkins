def call(body){
    def gitB_name = env.GIT_BRANCH
    def gitmodulesContent = readFile('.gitmodules')
    // Define a regular expression to match submodule entries
    def regex = /\[submodule "(.*?)"]\n\s*path = (.*?)\n\s*url = (.*?)\n/
    // Use a matcher to find all submodule entries
    def matcher = (gitmodulesContent =~ regex)
    // Iterate through matches and print path and url values
    matcher.each { match ->
        def submodule = [:]
        submodule['path'] = match[2]
        submodule['url'] = match[3]

        println "Path: ${submodule['path']}"
        println "URL: ${submodule['url']}"
        println '---'
    }
    println "${submodule}"
}