#!/bin/bash
set -ex

MITREID_CNAF_REPO=${MITREID_CNAF_REPO:-https://github.com/indigo-iam/OpenID-Connect-Java-Spring-Server.git}
MITREID_CNAF_BRANCH=${MITREID_CNAF_BRANCH:-develop-1.3.x}
BUILD_LOG=/tmp/travis-mitre-build.out

error_handler() {
  echo ERROR: An error was encountered bulding mitreid 
  tail -1000 ${BUILD_LOG}
  exit 1
}

trap 'error_handler' ERR
git clone ${MITREID_CNAF_REPO} ${HOME}/mitreid-cnaf
pushd ${HOME}/mitreid-cnaf
git checkout ${MITREID_CNAF_BRANCH}
mvn install > ${BUILD_LOG} 2>&1 || error_handler
popd
