/////////////////////////////////////////////////////////////////////////////
// Utility Functions

/////////////////////////////////////////////////////////////////////////////
// Import Constants

// Load the constants
new GroovyShell().evaluate(new File("${this.getClass().protectionDomain.codeSource.location.path}/seedConstants.groovy"))

def sinfoOneRateLimitBuilds(Map config) {
  if (config.rateLimitBuildsCount) {
    return { delegate ->
      delegate.rateLimitBuilds {
        throttle {
          count(config.rateLimitBuildsCount)
          durationName(config.rateLimitBuildsDurationName ?: 'day')
          userBoost(config.rateLimitBuildsUserBoost ?: false)
        }
      }
    }
  }
  return null
}

def sinfoOneGit(Map config) {
  final configLibrary = config?.library ?: false
  final configCps = config?.cps ?: false
  final configBranch = config?.branch ?: K.DEFAULT_GIT_BRANCH
  final configUrl = "${K.GIT_URL_PREFIX}/${config.path}.git"
  if (configLibrary) {
    return {
      remote(configUrl)
      credentialsId(K.JENKINS_GITLAB_CREDENTIALS)
    }
  }
  if (configCps) {
    return {
      remote {
        url(configUrl)
        credentials(K.JENKINS_GITLAB_CREDENTIALS)
      }
      branch(configBranch)
    }
  }
  return {}
}

def sinfoOnePipelineLib() {
  return {
    name('sinfo-one-lib')
    defaultVersion('master')
    allowVersionOverride(true)
    retriever {
      modernSCM {
        scm {
          git sinfoOneGit (
            library: true,
            path: 'FDS/internship/devops/jenkins-pipelines'
            //path: 'FDS/DevOps/jenkins-pipelines'
          )
        }
        libraryPath('pipeline-lib')
      }
    }
  }
}