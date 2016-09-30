#!/bin/bash
set -ex

# The current script directory
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}"  )" && pwd  )"
cd ${DIR}

default_image_name="indigoiam/iam-login-service"

IAM_LOGIN_SERVICE_IMAGE=${IAM_LOGIN_SERVICE_IMAGE:-${default_image_name}}

pushd ../../
POM_VERSION="v$(sh utils/print-pom-version.sh)"
popd

GIT_COMMIT_SHA=$(git rev-parse --short HEAD)

POM_VERSION_TAG=${IAM_LOGIN_SERVICE_IMAGE}:${POM_VERSION}-${GIT_COMMIT_SHA}
POM_VERSION_LATEST_TAG=${IAM_LOGIN_SERVICE_IMAGE}:${POM_VERSION}-latest

if [[ -n ${DOCKER_REGISTRY_HOST} ]]; then
  docker tag ${POM_VERSION_TAG} ${DOCKER_REGISTRY_HOST}/${POM_VERSION_TAG}
  docker tag ${POM_VERSION_LATEST_TAG} ${DOCKER_REGISTRY_HOST}/${POM_VERSION_LATEST_TAG}

  docker push ${DOCKER_REGISTRY_HOST}/${POM_VERSION_TAG}
  docker push ${DOCKER_REGISTRY_HOST}/${POM_VERSION_LATEST_TAG}
else
  docker push ${POM_VERSION_TAG}
  docker push ${POM_VERSION_LATEST_TAG}
fi
