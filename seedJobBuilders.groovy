/////////////////////////////////////////////////////////////////////////////
// Job Builders

/////////////////////////////////////////////////////////////////////////////
// Import Utility Functions

// Load the utility functions
def utilityFunctions = new GroovyShell().evaluate(new File("${this.getClass().protectionDomain.codeSource.location.path}/seedUtilityFunctions.groovy"))

def sinfoOneJenkinsPrune(Map config) {
  final configName = "/jenkins/${config.name}"
  final configDescription = "Jenkins ${config.name}"
  return freeStyleJob(configName) {
    description(configDescription)
    logRotator{
      numToKeep(3)
    }
    triggers {
      if (config.triggerCron != null) {
        cron {
          spec(config.triggerCron)
        }
      }
    }
    steps {
      shell('docker system prune -f -a')
    }
  }
}

def sinfoOnePipelineDockerImage(Map config) {
  //final configName = "/FDS/DevOps/docker-images/${config.name}"
  final configName = "/FDS/internship/devops/docker-images/${config.name}"
  final configDescription = "SiFides Docker image ${config.name}"
  final rateLimitBuildsBody = utilityFunctions.sinfoOneRateLimitBuilds(config)
  return pipelineJob(configName) {
    description(configDescription)
    logRotator{
      numToKeep(10)
    }
    properties {
      pipelineTriggers {
        triggers {
          if (config.triggerCron != null) {
            cron {
              spec(config.triggerCron)
            }
          }
          if (config.triggerUpstream != null) {
            upstream {
              upstreamProjects(config.triggerUpstream)
              threshold('SUCCESS')
            }
          }
        }
      }
      if (rateLimitBuildsBody) {
        rateLimitBuildsBody(delegate)
      }
    }
    parameters {
        string {
            name('gitBranch')
            description('Git branch')
            defaultValue('master')
        }
    }
    definition {
      cpsScm {
        scm {
          git utilityFunctions.sinfoOneGit (
            cps: true,
            path: 'FDS/internship/devops/jenkins-pipelines'
            //path: 'FDS/DevOps/jenkins-pipelines'
          )
        }
        scriptPath('docker-images/Jenkinsfile')
      }
    }
  }
}