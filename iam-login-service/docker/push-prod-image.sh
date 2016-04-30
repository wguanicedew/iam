#!/bin/bash
set -ex

docker tag -f italiangrid/iam-server ${DOCKER_REGISTRY_HOST}/italiangrid/iam-server
docker push ${DOCKER_REGISTRY_HOST}/italiangrid/iam-server

