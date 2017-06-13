pipeline {
  agent { label 'maven' }

  options {
    timeout(time: 1, unit: 'HOURS')
    buildDiscarder(logRotator(numToKeepStr: '5')) 
  }

  stages {

    stage('build') {
      steps {
        git(url: 'https://github.com/indigo-iam/iam.git', branch: env.BRANCH_NAME)

        sh 'mvn -B -U clean compile'

      }
    }

    stage('test') {
      steps {
        sh 'mvn -B test'
      }
    }

    stage('package') {
      steps {
        sh 'mvn -B -DskipTests=true package'
        archive 'iam-login-service/target/iam-login-service.war'
        archive 'iam-login-service/target/classes/iam.version.properties'
        archive 'iam-test-client/target/iam-test-client.jar'
      }
    }
  }

  post {
    always {
      junit '**/target/surefire-reports/TEST-*.xml'
    }

    success {
      slackSend channel: "#iam", color: 'good', message: "${env.JOB_NAME} - #${env.BUILD_NUMBER} Success (<${env.BUILD_URL}|Open>)" 
    }

    unstable {
      slackSend channel: "#iam", color: 'danger', message: "${env.JOB_NAME} - #${env.BUILD_NUMBER} Unstable (<${env.BUILD_URL}|Open>)" 
    }

    failure {
      slackSend channel: "#iam", color: 'danger', message: "${env.JOB_NAME} - #${env.BUILD_NUMBER} Failure (<${env.BUILD_URL}|Open>)" 
    }
  }
}
