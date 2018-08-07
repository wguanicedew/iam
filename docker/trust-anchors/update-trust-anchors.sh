#!/bin/bash
set -ex

if [[ -z "${FORCE_TRUST_ANCHORS_UPDATE}" ]]; then
  echo "Skipping trust anchors update (default behaviour)."
  exit 0
fi

fetch-crl --verbose || true

# Update centos ca-trust

for c in /etc/grid-security/certificates/*.pem; do
  cp $c /etc/pki/ca-trust/source/anchors/
done

update-ca-trust extract

if [ $# -gt 0 ]; then
  echo "Certificate copy requested to $1"
  rsync -avu --no-owner --no-group /etc/grid-security/certificates/ $1
fi
