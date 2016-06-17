#!/bin/bash
set -ex

if [[ -z ${IAM_LOGIN_SERVICE_IMAGE} ]]; then
  echo "Please set the IAM_LOGIN_SERVICE_IMAGE env variable"
  exit 1
fi

if [[ -n ${DOCKER_REGISTRY_HOST} ]]; then
  docker tag ${IAM_LOGIN_SERVICE_IMAGE} ${DOCKER_REGISTRY_HOST}/${IAM_LOGIN_SERVICE_IMAGE}
  docker push ${DOCKER_REGISTRY_HOST}/${IAM_LOGIN_SERVICE_IMAGE}
else
  docker push ${IAM_LOGIN_SERVICE_IMAGE}
fi
