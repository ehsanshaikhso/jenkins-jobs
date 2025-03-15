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
            def repoUrl = "https://shaikhm:EXQzY8fryUztgEnbPV2E@gitlab.sinfo-one.it/FDS/internship/devops/jenkins-pipelines.git"
            def branch = "master"
            def workspace = "/usr/share/jenkins/jenkins-pipelines"

            def runCommand(command, workingDir) {
                def process = new ProcessBuilder(command.split(" "))
                    .directory(new File(workingDir))
                    .redirectErrorStream(true)
                    .start()

                process.inputStream.eachLine { println it }
                process.waitFor()
            }

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
EOF
        ''')


    shell('echo "Listing files after writing Groovy script:" && ls -l')

    shell('''if [ ! -f "jenkins-cli.jar" ]; then
        echo "Downloading Jenkins CLI..."
        curl -o jenkins-cli.jar http://controller:8080/jnlpJars/jenkins-cli.jar
      else
        echo "Jenkins CLI already exists."
      fi
    ''')

    shell('''echo "Running Groovy script..."
      java -jar jenkins-cli.jar -auth shaikhm:1176294762b568f48cf934684e3d7c7ebb -s "${JENKINS_URL}" groovy = < jcasc-refresher.groovy
    ''')

    shell('''echo "Reloading Jenkins Configuration as Code..."
      java -jar jenkins-cli.jar -auth shaikhm:1176294762b568f48cf934684e3d7c7ebb -s "${JENKINS_URL}" reload-jcasc-configuration
    ''')
  }
}
