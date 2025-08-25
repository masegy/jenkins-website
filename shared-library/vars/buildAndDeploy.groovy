def call() {
    def runRemainingStages = true
    def appname = parseGitUrlAppname(env.GITURL)
    def appsurl = parseGitUrlAppUrl(env.GITURL)
    def targetBranch = env.TARGET_BRANCH

    pipeline {
        agent {
            label 'master'
        }

        stages {
            stage('Get Config Env') {
                steps {
                    script {
                        cleanWs()
                        currentBuild.displayName = "$appname-" + env.TARGET_BRANCH + "#" + env.BUILD_NUMBER
                    }
                }
            }

            stage('Cloning Source'){ 
                when {
                    expression {
                        runRemainingStages
                    }
                }
                steps{
                    withCredentials([string(credentialsId: 'secret-git', variable: 'GITLAB_TOKEN')]) {
                        sh """
                            git clone --depth 1 --single-branch --branch ${env.TARGET_BRANCH} https://oauth2:${GITLAB_TOKEN}@$appsurl ${env.TARGET_BRANCH}
                        """
                    }
                } 
            } 

            stage('Unit Test') {
                when {
                    expression {
                        runRemainingStages
                    }
                }
                steps {
                    script {
                        sh """
                            cd ${env.TARGET_BRANCH}
                            export NODE_OPTIONS=--max_old_space_size=5120
                            npm install --verbose
                            npm run test
                        """
                    }
                }
            }

            stage('Build') {
                when {
                    allOf{
                        expression {runRemainingStages}
                    }
                    
                }
                steps {
                    script {
                        sh """
                            cd ${env.TARGET_BRANCH}
                            npm run build:$buildEnv
                        """
                    }
                }
            }

            stage('Build Image & Push Repository'){
                when {
                    allOf {
                        expression{runRemainingStages}
                    }
                }
                steps{
                    script {
                        sh """
                        cd ${WORKSPACE}/${env.TARGET_BRANCH}
                        docker build -t $appname -f dockerfile .
                        """
                        echo "Success!"
                    }
                }
            }
        }
    }
}

private void parseGitUrlAppname(String gitUrl) {
    def matcher = (gitUrl =~ /.*\/(.*)\.git/)
    if (matcher.matches()) {
        return matcher[0][1]
    } else {
        error("Cannot parse appname from GITURL: ${gitUrl}")
    }
}

private void parseGitUrlAppUrl(String gitUrl) {
    def matcher = (gitUrl =~ /https:\/\/(.*)/)
    if (matcher.matches()) {
        return matcher[0][1]
    } else {
        error("Cannot parse appurl from GITURL: ${gitUrl}")
    }
}
