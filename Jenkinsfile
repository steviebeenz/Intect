pipeline {
  agent any
  stages {
    stage('Pull') {
      steps {
        git(url: 'https://github.com/SquareCodeFX/Intect', branch: 'master', changelog: true, poll: true)
      }
    }

    stage('Build') {
      steps {
        sh '/opt/maven-3.8.1/bin/mvn -B -DskipTests clean package'
      }
    }

    stage('Archive') {
      steps {
        archiveArtifacts(artifacts: 'target/Intect.jar', onlyIfSuccessful: true)
      }
    }

  }
  tools {
    maven 'maven'
    jdk 'Java8'
  }
}
