#!/bin/bash
set -ex

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}"  )" && pwd  )"
cd ${DIR}/../..
pwd
POM_VERSION=$(sh utils/print-pom-version.sh) 

mvn clean package
cp iam-login-service/target/iam-login-service-${POM_VERSION}.war ${DIR}

cd ${DIR}

docker build -f Dockerfile.prod --build-arg JAR_VERSION=${POM_VERSION} --rm=true \
  --no-cache=true -t italiangrid/iam-server .

rm ${DIR}/iam-login-service-${POM_VERSION}.war
