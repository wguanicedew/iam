#!/bin/bash
set -ex

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}"  )" && pwd  )"

if [[ -z ${IAM_TEST_CLIENT_JAR} ]]; then
  LOCAL_BUILD_JAR=${DIR}/../target/iam-test-client.jar
  if [[ -r ${LOCAL_BUILD_JAR} ]]; then
    IAM_TEST_CLIENT_JAR=${LOCAL_BUILD_JAR}
  fi
fi

if [[ ! -r ${IAM_TEST_CLIENT_JAR} ]]; then
  echo "Please set the IAM_TEST_CLIENT_JAR env variable"
  exit 1
fi

echo "Building image using jar from ${IAM_TEST_CLIENT_JAR}"

# Default name for iam login service docker image
default_image_name="indigoiam/iam-test-client"

IAM_TEST_CLIENT_IMAGE=${IAM_TEST_CLIENT_IMAGE:-${default_image_name}}
cd ${DIR}
cp ${IAM_TEST_CLIENT_JAR} iam-test-client.jar

docker build -f Dockerfile.prod \
  --rm=true --no-cache=true \
  -t ${IAM_TEST_CLIENT_IMAGE} .

rm iam-test-client.jar
