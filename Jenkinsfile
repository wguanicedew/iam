pipeline {
  agent { label 'maven' }

  options {
    timeout(time: 1, unit: 'HOURS')
    buildDiscarder(logRotator(numToKeepStr: '5')) 
  }

  stages{

    stage('build') {
      steps {
        git(url: 'https://github.com/indigo-iam/iam.git', branch: env.BRANCH_NAME)

        sh 'mvn -B -U clean package'

        junit '**/target/surefire-reports/TEST-*.xml'

        archive 'iam-login-service/target/iam-login-service.war'
        archive 'iam-login-service/target/classes/iam.version.properties'
        archive 'iam-test-client/target/iam-test-client.jar'
      }
    }
  }
}
