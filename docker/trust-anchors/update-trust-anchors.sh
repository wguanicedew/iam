#!/bin/bash
set -ex

if [ -n "${SKIP_UPDATE_TRUST_ANCHORS}" ]; then
  echo "Skipping trust anchors update as requested."
  exit 0
fi

fetch-crl --verbose || true

echo "Args: $@"

if [ $# -gt 0 ]; then
  rsync -avu --no-owner --no-group /etc/grid-security/certificates/ $1
fi
