#!/bin/bash

if [ -z ${SONAR_HOST_URL} ]; then
  echo "Please define the SONAR_HOST_URL env variable pointing to a valid sonarqube server"
  exit 1
fi

if [ -z ${SONAR_AUTH_TOKEN} ]; then
  echo "Please define the SONAR_AUTH_TOKEN env variable"
  exit 1
fi

mvn -B -U clean cobertura:cobertura \
  -Dcobertura.report.format=xml \
  checkstyle:check -Dcheckstyle.config.location=google_checks.xml \
  compile sonar:sonar \
  -Dsonar.host.url=${SONAR_HOST_URL} \
  -Dsonar.login=${SONAR_AUTH_TOKEN} 
