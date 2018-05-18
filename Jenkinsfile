#!/usr/bin/env groovy

def readProperty(filename, prop) {
  def value = sh script: "cat ${filename} | grep ${prop} | cut -d'=' -f2-", returnStdout: true
  return value.trim()
}

def jsonParse(url, basicAuth, field) {
  def value =  sh script: "curl -s -u '${basicAuth}' '${url}' | jq -r '${field}'", returnStdout: true
  return value.trim()
}

pipeline {

  agent { label 'maven' }

  options {
    timeout(time: 1, unit: 'HOURS')
    buildDiscarder(logRotator(numToKeepStr: '5'))
  }

  parameters {
    choice(name: 'RUN_SONAR', choices: 'yes\nno', description: 'Run Sonar static analysis')
  }
  
  environment {
    DOCKER_REGISTRY_HOST = "${env.DOCKER_REGISTRY_HOST}"
  }

  stages {
    stage('checkout') {
      steps {
        container('maven-runner'){
          deleteDir()
          checkout scm
          stash name: 'code', useDefaultExcludes: false
        }
      }
    }

    stage('build') {
      steps {
        container('maven-runner') {
          sh 'mvn -B clean compile'
        }
      }
    }

    stage('test') {
      steps {
        container('maven-runner') {
          sh 'mvn -B clean test'
        }
      }

      post {
        always {
          container('maven-runner') {
            junit '**/target/surefire-reports/TEST-*.xml'
          }
        }
      }
    }

    stage('PR analysis'){
      when{
        not {
          environment name: 'CHANGE_URL', value: ''
        }
      }
      steps {
        container('maven-runner') {
          script{
            def tokens = "${env.CHANGE_URL}".tokenize('/')
            def organization = tokens[tokens.size()-4]
            def repo = tokens[tokens.size()-3]

            withCredentials([string(credentialsId: '630f8e6c-0d31-4f96-8d82-a1ef536ef059', variable: 'GITHUB_ACCESS_TOKEN')]) {
              withSonarQubeEnv{
                sh """
                  mvn -B -U clean compile sonar:sonar \\
                    -Dsonar.analysis.mode=preview \\
                    -Dsonar.github.pullRequest=${env.CHANGE_ID} \\
                    -Dsonar.github.repository=${organization}/${repo} \\
                    -Dsonar.github.oauth=${GITHUB_ACCESS_TOKEN} \\
                    -Dsonar.host.url=${SONAR_HOST_URL} \\
                    -Dsonar.login=${SONAR_AUTH_TOKEN}
                """
              }
            }
          }
        }
      }
    }

    stage('analysis'){
      when{
        expression {
          return "yes" == "${params.RUN_SONAR}"
        }
        anyOf { branch 'master'; branch 'develop' }
        environment name: 'CHANGE_URL', value: ''
      }
      steps {
          container('maven-runner') {
            script{
              def cobertura_opts = 'cobertura:cobertura -Dmaven.test.failure.ignore -DfailIfNoTests=false -Dcobertura.report.format=xml'
              def checkstyle_opts = 'checkstyle:check -Dcheckstyle.config.location=google_checks.xml'
              def sonarBasicAuth

              withSonarQubeEnv{
                sonarBasicAuth  = "${SONAR_AUTH_TOKEN}:"

                sh "mvn clean -U ${cobertura_opts} ${checkstyle_opts} ${SONAR_MAVEN_GOAL} -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.login=${SONAR_AUTH_TOKEN}"
              }

              def sonarServerUrl = readProperty('target/sonar/report-task.txt', 'serverUrl')
              def ceTaskUrl = readProperty('target/sonar/report-task.txt', 'ceTaskUrl')

              timeout(time: 5, unit: 'MINUTES') {
                waitUntil {
                  def result = jsonParse(ceTaskUrl, sonarBasicAuth, '.task.status')
                  echo "Current CeTask status: ${result}"
                  return "SUCCESS" == "${result}"
                }
              }

              def analysisId = jsonParse(ceTaskUrl, sonarBasicAuth, '.task.analysisId')
              echo "Analysis ID: ${analysisId}"

              def url = "${sonarServerUrl}/api/qualitygates/project_status?analysisId=${analysisId}"
              def qualityGate =  jsonParse(url, sonarBasicAuth, '')
              echo "${qualityGate}"

              def status =  jsonParse(url, sonarBasicAuth, '.projectStatus.status')

              if ("ERROR" == "${status}") {
                currentBuild.result = 'FAILURE'
                error 'Quality Gate failure'
              }
            }
          }
        }
    }
    
    stage('license-check') {

      steps {
        container('maven-runner') {
          sh 'mvn license:check'
        }
    }

    stage('package') {
      steps {
        container('maven-runner') {
          sh 'mvn -B -DskipTests=true clean package' 
          archive 'iam-login-service/target/iam-login-service.war'
          archive 'iam-login-service/target/classes/iam.version.properties'
          archive 'iam-test-client/target/iam-test-client.jar'
          stash includes: 'iam-login-service/target/iam-login-service.war,iam-login-service/target/classes/iam.version.properties,iam-test-client/target/iam-test-client.jar', name: 'iam-artifacts'
        }
      }
    }

    stage('docker-images') {
      agent { label 'docker' }
      steps {
        container('docker-runner') {
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
