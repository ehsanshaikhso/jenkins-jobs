// Doc: https://jenkinsci.github.io/job-dsl-plugin
// Doc: https://jenkins.dev.aws.sinfo-one.it/plugin/job-dsl/api-viewer/index.html
// Doc: http://docs.groovy-lang.org/docs/latest/html/documentation/core-domain-specific-languages.html

/////////////////////////////////////////////////////////////////////////////
// Import Utility Functions and Job Builders

// Load the utility functions
def utilityFunctions = new GroovyShell().evaluate(new File("${this.getClass().protectionDomain.codeSource.location.path}/seedUtilityFunctions.groovy"))

// Load the job builders
def jobBuilders = new GroovyShell().evaluate(new File("${this.getClass().protectionDomain.codeSource.location.path}/seedJobBuilders.groovy"))


/////////////////////////////////////////////////////////////////////////////
// Folders

folder('/FDS') {
  description('Service Line Fides')
  properties {
    folderLibraries {
      libraries {
        libraryConfiguration utilityFunctions.sinfoOnePipelineLib()
      }
    }
  }
}

folder('/FDS/DevOps') {
  description('SiFides DevOps')
}

folder('/FDS/DevOps/docker-images') {
  description('SiFides Docker Images')
}

/////////////////////////////////////////////////////////////////////////////
// Jobs

seedJobBuilders.sinfoOneJenkinsPrune name: 'prune', triggerCron: 'H 0 * * *'

seedJobBuilders.sinfoOnePipelineDockerImage name: 'debian' , triggerCron: 'H 0 1 * *'
seedJobBuilders.sinfoOnePipelineDockerImage name: 'debian-builder', triggerUpstream: 'debian'
seedJobBuilders.sinfoOnePipelineDockerImage name: 'debian-dev', triggerUpstream: 'debian-builder'
seedJobBuilders.sinfoOnePipelineDockerImage name: 'fides-builder', triggerUpstream: 'debian-dev'
seedJobBuilders.sinfoOnePipelineDockerImage name: 'iscobol-builder', triggerUpstream: 'debian-dev'
seedJobBuilders.sinfoOnePipelineDockerImage name: 'spring-boot-builder', triggerUpstream: 'debian-dev'
seedJobBuilders.sinfoOnePipelineDockerImage name: 'fides-web-server'
seedJobBuilders.sinfoOnePipelineDockerImage name: 'fides-license-server'
seedJobBuilders.sinfoOnePipelineDockerImage name: 'fides-legacy-server'
seedJobBuilders.sinfoOnePipelineDockerImage name: 'fides-legacy-webclient'
seedJobBuilders.sinfoOnePipelineDockerImage name: 'fides-legacy-rest-server'
seedJobBuilders.sinfoOnePipelineDockerImage name: 'fides-legacy-crpp-gimm'
seedJobBuilders.sinfoOnePipelineDockerImage name: 'httpd' , triggerUpstream: 'debian-dev'
seedJobBuilders.sinfoOnePipelineDockerImage name: 'httpd-acmesh', triggerUpstream: 'httpd',
  rateLimitBuildsCount: 1, rateLimitBuildsDurationName: 'week'
seedJobBuilders.sinfoOnePipelineDockerImage name: 'svnedge'
