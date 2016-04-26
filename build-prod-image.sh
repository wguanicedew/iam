#!/bin/sh
docker build -f ./docker/iam/Dockerfile.prod --rm=true --no-cache=true -t italiangrid/iam-server .
docker tag -f italiangrid/iam-server ${DOCKER_REGISTRY_HOST}/italiangrid/iam-server
