#!/bin/bash
set -ex

# The current script directory
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}"  )" && pwd  )"

if [[ -z ${IAM_LOGIN_SERVICE_WAR} ]]; then
  LOCAL_BUILD_WAR=${DIR}/../target/iam-login-service.war
  if [[ -r ${LOCAL_BUILD_WAR} ]]; then
    IAM_LOGIN_SERVICE_WAR=${LOCAL_BUILD_WAR}
  fi
fi

if [[ ! -r ${IAM_LOGIN_SERVICE_WAR} ]]; then
  echo "Please set the IAM_LOGIN_SERVICE_WAR env variable so that it points to the IAM login service war location"
  exit 1
fi

echo "Building image using war from ${IAM_LOGIN_SERVICE_WAR}"

# Default name for iam login service docker image
default_image_name="indigoiam/iam-login-service"

# Env variable setting the IAM Login service image name
IAM_LOGIN_SERVICE_IMAGE=${IAM_LOGIN_SERVICE_IMAGE:-${default_image_name}}

cd ${DIR}
cp ${IAM_LOGIN_SERVICE_WAR} iam-login-service.war

docker build -f Dockerfile.prod \
  --rm=true --no-cache=true \
  -t ${IAM_LOGIN_SERVICE_IMAGE} .

if [[ -z ${IAM_LOGIN_SERVICE_VERSION} ]]; then
  pushd ../../
  POM_VERSION="v$(sh utils/print-pom-version.sh)"
  popd
else
  POM_VERSION="${IAM_LOGIN_SERVICE_VERSION}"
fi

GIT_COMMIT_SHA=$(git rev-parse --short HEAD)
GIT_BRANCH_NAME=$(git rev-parse --abbrev-ref HEAD | sed 's#/#_#g')

docker tag ${IAM_LOGIN_SERVICE_IMAGE} ${IAM_LOGIN_SERVICE_IMAGE}:${POM_VERSION}-${GIT_COMMIT_SHA}
docker tag ${IAM_LOGIN_SERVICE_IMAGE} ${IAM_LOGIN_SERVICE_IMAGE}:${POM_VERSION}-latest
docker tag ${IAM_LOGIN_SERVICE_IMAGE} ${IAM_LOGIN_SERVICE_IMAGE}:${GIT_BRANCH_NAME}-latest

rm iam-login-service.war
