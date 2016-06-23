#!/bin/bash
set -ex

default_image_name="indigoiam/iam-login-service"

IAM_LOGIN_SERVICE_IMAGE=${IAM_LOGIN_SERVICE_IMAGE:-${default_image_name}}

if [[ -n ${DOCKER_REGISTRY_HOST} ]]; then
  docker tag ${IAM_LOGIN_SERVICE_IMAGE} ${DOCKER_REGISTRY_HOST}/${IAM_LOGIN_SERVICE_IMAGE}
  docker push ${DOCKER_REGISTRY_HOST}/${IAM_LOGIN_SERVICE_IMAGE}
else
  docker push ${IAM_LOGIN_SERVICE_IMAGE}
fi
