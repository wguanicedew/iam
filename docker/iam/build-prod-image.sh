#!/bin/bash
set -ex

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}"  )" && pwd  )"

echo "Building in $(pwd)"

cd ${DIR}/../.. && mvn clean package && cp target/iam-login-service*.war ${DIR}
cd ${DIR}
docker build -f Dockerfile.prod --rm=true --no-cache=true -t italiangrid/iam-server .
rm ${DIR}/iam-login-service*.war
docker tag -f italiangrid/iam-server ${DOCKER_REGISTRY_HOST}/italiangrid/iam-server
