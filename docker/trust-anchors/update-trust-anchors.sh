#!/bin/bash
set -ex

fetch-crl --verbose || true

echo "Args: $@"

if [ $# -gt 0 ]; then
  rsync -avu --no-owner --no-group /etc/grid-security/certificates/ $1
fi
