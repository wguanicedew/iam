# INDIGO Identity and Access Management (IAM) service

![jenkins-build-status-develop](https://ci.cloud.cnaf.infn.it/job/iam/job/develop/badge/icon)
![sonarqube-qg](https://sonar.cloud.cnaf.infn.it/api/badges/gate?key=it.infn.mw%3Aiam-parent)
![sonarqube-coverage](https://sonar.cloud.cnaf.infn.it/api/badges/measure?key=it.infn.mw%3Aiam-parent&metric=coverage)

The INDIGO IAM is an Identity and Access Management service first developed in the
context of the [INDIGO-Datacloud H2020 project][indigo-datacloud].

## Main features

- OpenID connect provider based on the [MitreID OpenID connect library][mitreid]
- [SCIM][scim] user provisioning and management APIs
- SAML authentication support
- Google authentication support 
- [OAuth token exchange][token-exchange] support

## What's new

See the [changelog](CHANGELOG.md).

## Build instructions

See the [developer guide](doc/developer.md).

## License

[Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)

[indigo-datacloud]: https://www.indigo-datacloud.eu/ 
[mitreid]: https://github.com/mitreid-connect/OpenID-Connect-Java-Spring-Server
[scim]: http://www.simplecloud.info/
[token-exchange]: https://tools.ietf.org/html/draft-ietf-oauth-token-exchange-09
