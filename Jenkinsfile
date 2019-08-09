#!/usr/bin/env groovy
@Library('sd')_
def kubeLabel = getKubeLabel()

pipeline {
  agent {
    kubernetes {
      label "${kubeLabel}"
      cloud 'Kube mwdevel'
      defaultContainer 'runner'
      inheritFrom 'ci-template'
      containerTemplate {
            name 'runner'
            image 'cloud-vm114.cloud.cnaf.infn.it/cnafsd/centos7-jenkins-slave:latest'
            ttyEnabled true
            command 'cat'
            resourceRequestCpu '2'
            resourceLimitCpu '2'
            resourceRequestMemory '2500Mi'
            resourceLimitMemory '2500Mi'
      }
    }
  }

  options {
    timeout(time: 1, unit: 'HOURS')
    buildDiscarder(logRotator(numToKeepStr: '5'))
  }

  parameters {
    booleanParam(name: 'SONAR_ANALYSIS', defaultValue: false, description: 'Run Sonar Analsysis')
  }

  triggers { cron('@daily') }

  environment {
    DOCKER_REGISTRY_HOST = "${env.DOCKER_REGISTRY_HOST}"
    SONAR_USER_HOME = "${env.WORKSPACE}/.sonar"
  }

  stages {
    stage('checkout') {
      steps {
          deleteDir()
          checkout scm
          stash name: 'code', useDefaultExcludes: false
      }
    }

    stage('license-check') {
      steps {
          sh 'mvn license:check'
      }
    }

    stage('compile') {
      steps {
        sh 'mvn compile'
      }
    }

    stage('test') {
      when{
        not {
          expression{ return params.SONAR_ANALYSIS }
        }
      }

      steps {
        sh 'mvn test'
      }

      post {
        always {
          junit '**/target/surefire-reports/TEST-*.xml'
          step( [ $class: 'JacocoPublisher' ] )
        }
        unsuccessful {
          archiveArtifacts artifacts:'**/**/*.dump'
          archiveArtifacts artifacts:'**/**/*.dumpstream'
        }
      }
    }

    stage('PR analysis'){
      when{
        allOf{
          expression{ env.CHANGE_URL != ''}
          expression{ return params.SONAR_ANALYSIS }
        }
      }
      steps {
        script{
          def tokens = "${env.CHANGE_URL}".tokenize('/')
          def organization = tokens[tokens.size()-4]
          def repo = tokens[tokens.size()-3]

          withCredentials([string(credentialsId: '630f8e6c-0d31-4f96-8d82-a1ef536ef059', variable: 'GITHUB_ACCESS_TOKEN')]) {
            withSonarQubeEnv('sonarcloud.io'){
              sh """
                mvn -B -U install sonar:sonar \\
                  -Dsonar.github.pullRequest=${env.CHANGE_ID} \\
                  -Dsonar.github.repository=${organization}/${repo} \\
                  -Dsonar.github.oauth=${GITHUB_ACCESS_TOKEN} \\
                  -Dsonar.host.url=${SONAR_HOST_URL} \\
                  -Dsonar.login=${SONAR_AUTH_TOKEN} \\
                  -Dsonar.branch.name=${BRANCH_NAME} \\
                  -Dsonar.branch.target=develop \\
                  -Dsonar.projectKey=indigo-iam_iam \\
                  -Dsonar.organization=indigo-iam
              """
            }
          }
        }
      }

      post {
        always {
          junit '**/target/surefire-reports/TEST-*.xml'
          step( [ $class: 'JacocoPublisher' ] )
        }
        unsuccessful {
          archiveArtifacts artifacts:'**/**/*.dump'
          archiveArtifacts artifacts:'**/**/*.dumpstream'
        }
      }
    }

    stage('analysis'){

      when{
        allOf{
          expression{ env.CHANGE_URL == ''}
          expression{ return params.SONAR_ANALYSIS }
        }
      }

      steps {
        script{
          def checkstyle_opts = 'checkstyle:check -Dcheckstyle.config.location=google_checks.xml'

            withSonarQubeEnv('sonarcloud.io'){
              sh """
                mvn -U ${checkstyle_opts} \\
                install sonar:sonar \\
                -Dsonar.host.url=${SONAR_HOST_URL} \\
                -Dsonar.login=${SONAR_AUTH_TOKEN} \\
                -Dsonar.branch.name=${BRANCH_NAME} \\
                -Dsonar.projectKey=indigo-iam_iam \\
                -Dsonar.organization=indigo-iam
              """
            }
        }
      }
      post {
        always {
          junit '**/target/surefire-reports/TEST-*.xml'
            step( [ $class: 'JacocoPublisher' ] )
        }
        unsuccessful {
          archiveArtifacts artifacts:'**/**/*.dump'
          archiveArtifacts artifacts:'**/**/*.dumpstream'
        }
      }
    }
    
    stage('quality-gate') {

      when{
        expression{ return params.SONAR_ANALYSIS }
      }

      steps {
        timeout(time: 5, unit: 'MINUTES') {
          waitForQualityGate abortPipeline: true
        }
      }
    }

    stage('package') {
      steps {
        sh 'mvn -B -DskipTests=true clean package' 
        archive 'iam-login-service/target/iam-login-service.war'
        archive 'iam-login-service/target/classes/iam.version.properties'
        archive 'iam-test-client/target/iam-test-client.jar'
        stash includes: 'iam-login-service/target/iam-login-service.war,iam-login-service/target/classes/iam.version.properties,iam-test-client/target/iam-test-client.jar', name: 'iam-artifacts'
      }
    }

    stage('docker-images') {
      steps {
        deleteDir()
        unstash 'code'
        unstash 'iam-artifacts'
        sh '''
        sed -i -e 's#iam\\.version#IAM_VERSION#' iam-login-service/target/classes/iam.version.properties
        source iam-login-service/target/classes/iam.version.properties
        export IAM_LOGIN_SERVICE_VERSION="v${IAM_VERSION}"

        /bin/bash iam-login-service/docker/build-prod-image.sh
        /bin/bash iam-login-service/docker/push-prod-image.sh
        /bin/bash iam-test-client/docker/build-prod-image.sh
        /bin/bash iam-test-client/docker/push-prod-image.sh
        '''
        script {
          if (env.BRANCH_NAME == 'master') {
            sh '''
            sed -i -e 's#iam\\.version#IAM_VERSION#' iam-login-service/target/classes/iam.version.properties
            source iam-login-service/target/classes/iam.version.properties
            export IAM_LOGIN_SERVICE_VERSION="v${IAM_VERSION}"
            unset DOCKER_REGISTRY_HOST
            /bin/bash iam-login-service/docker/push-prod-image.sh
            /bin/bash iam-test-client/docker/push-prod-image.sh
            '''
          }
        }
      }
    }
  }

  post {
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
