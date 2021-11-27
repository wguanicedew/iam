#!/bin/bash
set -e

IMAGES="indigoiam/iam-login-service-bp indigoiam/iam-test-client-bp indigoiam/voms-aa-bp"

# The current script directory
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}"  )" && pwd  )"

cd ${DIR}

if [[ $# -ne 1 ]]; then
  echo "This script requires a tag parameter"
  echo "Usage: push-images.sh v1.8.0.20211127"
  exit 1
fi

TAG=$1

POM_VERSION="v$(sh print-pom-version.sh)"
GIT_COMMIT_SHA=$(git rev-parse --short HEAD)

for img in ${IMAGES}; do 
  docker tag ${img}:${POM_VERSION}-${GIT_COMMIT_SHA} ${TAG}
  docker push ${img}:${TAG}
  docker push ${img}:${POM_VERSION}-${GIT_COMMIT_SHA}
done

