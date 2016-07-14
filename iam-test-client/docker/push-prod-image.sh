#!/bin/bash
set -ex

default_image_name="indigoiam/iam-test-client"
IAM_TEST_CLIENT_IMAGE=${IAM_TEST_CLIENT_IMAGE:-${default_image_name}}

if [[ -n ${DOCKER_REGISTRY_HOST} ]]; then
  docker tag ${IAM_TEST_CLIENT_IMAGE} ${DOCKER_REGISTRY_HOST}/${IAM_TEST_CLIENT_IMAGE}
  docker push ${DOCKER_REGISTRY_HOST}/${IAM_TEST_CLIENT_IMAGE}
else
  docker push ${IAM_TEST_CLIENT_IMAGE}
fi
