// Doc: https://jenkinsci.github.io/job-dsl-plugin
// Doc: https://jenkins.dev.aws.sinfo-one.it/plugin/job-dsl/api-viewer/index.html

folder('/jenkins') {
  description('Jenkins JCASC Refresher')
}

freeStyleJob('/jenkins/jcasc-refresher') {
  description('Jenkins JCASC Refresher')

  logRotator {
    numToKeep(10)
  }

  steps {
    shell('echo "Current directory: $(pwd)"') 
    
shell('''
            cat << 'EOF' > jcasc-refresher.groovy
            import com.cloudbees.plugins.credentials.CredentialsProvider
            import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials
            import hudson.model.User
            import jenkins.model.Jenkins
            def runCommand(command, workingDir) {
              def process = new ProcessBuilder(command.split(" "))
                    .directory(new File(workingDir))
                    .redirectErrorStream(true)
                    .start()
                  process.inputStream.eachLine { println it }
                  process.waitFor()
            }
            def credentialsId = "sinfo-one-jenkins-gitlab"
            def credentials = CredentialsProvider.lookupCredentials(
              UsernamePasswordCredentials.class,
              Jenkins.instance,
              null,
              null
            ).find { it.id == credentialsId }
            if(credentials){
              def repo_username_token = "${credentials.username}:${credentials.password.toString()}"
              def repoUrl = "https://"+repo_username_token+"@gitlab.sinfo-one.it/FDS/internship/devops/jenkins-pipelines.git"
              def branch = "master"
              def workspace = "/usr/share/jenkins/jenkins-pipelines"

              def repoDir = new File(workspace)

              if (!repoDir.exists() || repoDir.listFiles().length == 0) {
                  println "Repository Directory not found OR No Files exists in Repository Directory."
              } 
              else 
              {
                  println "Repository found. Pulling latest changes..."
                  runCommand("git pull ${repoUrl} ${branch}", workspace)
                  println "Pull completed successfully!"
              }
            } 
            else {println "Credentials not found!"}
EOF
        ''')
        
    shell('echo "Listing files after writing Groovy script:" && ls -l')

    shell('''if [ ! -f "jenkins-cli.jar" ]; then
        echo "Downloading Jenkins CLI..."
        curl -o jenkins-cli.jar $JENKINS_URL/jnlpJars/jenkins-cli.jar
      else
        echo "Jenkins CLI already exists."
      fi
    ''')

    shell('''echo "Running Groovy script..."
      java -jar jenkins-cli.jar -auth $JENKINS_AUTH -s "${JENKINS_URL}" groovy = < jcasc-refresher.groovy
    ''')

    shell('''echo "Reloading Jenkins Configuration as Code..."
      java -jar jenkins-cli.jar -auth $JENKINS_AUTH -s "${JENKINS_URL}" reload-jcasc-configuration
    ''')
  }
}
