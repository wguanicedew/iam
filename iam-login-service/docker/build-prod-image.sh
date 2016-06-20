#!/bin/bash
set -ex

# IAM_LOGIN_SERVICE_WAR mandatory env variable points the the war to be used to build the IAM login
# service image
if [[ ! -r ${IAM_LOGIN_SERVICE_WAR} ]]; then
  echo "Please set the IAM_LOGIN_SERVICE_WAR env variable so that it points to the IAM login service war location"
  exit 1
fi

# The current script directory
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}"  )" && pwd  )"

# Default name for iam login service docker image
default_image_name="indigoiam/iam-login-service"

# Env variable setting the IAM Login service image name
IAM_LOGIN_SERVICE_IMAGE=${IAM_LOGIN_SERVICE_IMAGE:-${default_image_name}}

cd ${DIR}
cp ${IAM_LOGIN_SERVICE_WAR} iam-login-service.war

docker build -f Dockerfile.prod \
  --rm=true --no-cache=true \
  -t ${IAM_LOGIN_SERVICE_IMAGE} .

rm iam-login-service.war
