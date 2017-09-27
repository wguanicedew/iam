# Changelog

## [Unreleased]

### Added

- The login button text can now be customised for local (#185) and SAML
  login (#177)
- A privacy policy can now be linked to the IAM login page (#182)
- Improved error pages rendering (#178)
- SAML metadata can now be filtered according to certain conditions (e.g.,
  SIRTFI compliance)
- The organisation name is now included in the IAM dashboard top bar (#186)
- IAM now implements a scope policy management API that allows to restrict the
  use of OAuth scopes only to selected users or group of users (#80)

### Fixed

- IAM now correctly enforces SAML metadata signature checks (#175)
- The subject of IAM notification messages now includes the organisation name
  (#163)

## 1.0.0 (2017-8-31)

This release provides improvements, bug fixes and new features:

- IAM now supports hierarchical groups. The SCIM group management API has been
  extended to support nested group creation and listing, and the IAM dashboard
  can now leverage these new API functions (#88)
- IAM now supports native X.509 authentication (#119) and the ability to
  link/unlink X.509 certificates to a user membership (#120)
- IAM now supports configurable on-demand account provisioning for trusted SAML
  IDPs; this means that the IAM can be configured to automatically on-board
  users from a trusted IdP/federation after a succesfull external
  authentication (i.e. no former registration or administration approval is
  required to on-board users) (#130)
- IAM now provides an enhanced token management and revocation API that can be
  used by IAM administrators to see and revoke active tokens in the system (#121)
- Account linking can be now be disabled via a configuration option (#142)
- IAM dashboard now correctly displays valid active access tokens for a user
  (#112) 
- A problem that caused IAM registration access tokens to expire after the
  first use has been fixed (#134)
- IAM now provides an endpoint than can be used to monitor the service
  connectivity to external service (ie. Google) (#150)
- Improved SAML metadata handling (#146) and reloading (#115)
- Account linking can now be disabled via a configuration option (#142)
- The IAM audit log now provides fine-grained information for many events
  (#137)
- The IAM token introspection endpoint now correctly supports HTTP form
  authentication (#149)
- Notes in registration requests are now required (#114) to make life easier
  for VO administrators that wants to understand the reason for a registration
  request
- Password reset emails now contain the username of the user that has requested
  the password reset (#108)
- A stronger SAML account linking logic is now in place (#116)
- Starting from this release, we provide RPM and Deb packages (#110) and a
  puppet module to configure the IAM service (#109)
- The spring-boot dependency has been updated to version 1.3.8.RELEASE (#144)
- An issue that prevented access to the token revocation endpoint has been
  fixed (#159)

## 0.6.0 (2017-3-31)

This release provides improvements and bug fixes:

- IAM now implements an audit log that keeps track of all interesting security
  events (#79)
- Password grant logins are now correctly logged (#98)
- The MitreID logic for resolving user access and refresh token has been
  replaced with a more efficient implementation (#94)
- Audience restrictions can be enforced on tokens obtained through all
  supported OAuth/OIDC flows (#92)
- The tokens and site approval cleanup periods are now configurable (#96)

## 0.5.0 (2016-12-6)

This release provides new functionality and bug fixes:

- It is now possible for users to link external authentication accounts
  (Google, SAML) to the user IAM account (#39)
- It is now possible to register at the IAM starting from an external
  authentication (#44)
- The IAM now exposes an authority management endpoint (integrated in the
  dashboard) that allows to assign/remove administrative rights to/from users
  (#46)
- The token exchange granter now enforces audience restrictions correctly (#32)
- It is now possible to set custom SAML maxAssertionTime and
  maxAuthenticationAge to customize how the SAML filter should check incoming
  SAML responses and assertions (#65)
- Improved token exchange documentation (#51,#52)
- The IAM now includes spring boot actuator endpoints that allow fine-grained
  monitoring of the status of the service (#62)
- Group creation in the dashboard now behaves as expected (#34)
- Editing first name and other information from the dashboard now behaves as
  expected (#57)
- The IAM now provides a refactored SAML WAYF service that remembers the identity
  provider chosen by the user (#59)
- The overall test coverage has been improved

## 0.4.0 (2016-09-30)

This release provides new functionality and some fixes:

- Groups are now encoded in the JSON returned by the IAM /userinfo
  endpoint as an array of group names.
- Group information is also exposed by the token introspection endpoint
- External authentication information (i.e. when a user authenticates with
  Google or SAML instead of username/password) is now provided in the JSON
  returned by the /userinfo endpoint
- The first incarnation of the administrative dashboard is now included in the
  service 
- The first incarnation of the registration service is now included. The
  registration service implements a "self-register with admin approval"
  registration flow 
- User passwords are now encoded in the database using the Bcrypt encoder
- A password forgotten service is now provided

More information about bug fixes and other developments can be found on
our [JIRA release board][jira-v0.4.0]

## 0.3.0 (2016-07-12)

This is the first public release of the INDIGO Identity and Access Management
Service.

The IAM is an OpenID-connect identity provider which provides:

- OpenID-connect and OAuth client registration and management (leveraging and
  extending the [MitreID connect server][mitre] functionality
- [SCIM][scim] user and group provisioning and management
- A partial implementation of the [OAuth Token Exchange draft
  standard][token-exchange] for OAuth token delegation and impersonation

The IAM is currently released as a [Docker image][iam-image] hosted on
Dockerhub.

Documentation on how to build and run the service can be found in the [IAM
GitBook manual][gitbook-manual] or on [Github][github-doc].

[iam-image]: https://hub.docker.com/r/indigodatacloud/iam-login-service
[mitre]: https://github.com/mitreid-connect/OpenID-Connect-Java-Spring-Server
[scim]: http://www.simplecloud.info
[token-exchange]: https://tools.ietf.org/html/draft-ietf-oauth-token-exchange-05
[gitbook-manual]: https://www.gitbook.com/book/andreaceccanti/iam/details
[github-doc]: https://github.com/indigo-iam/iam/blob/master/SUMMARY.md
[jira-v0.4.0]: https://issues.infn.it/jira/browse/INDIAM/fixforversion/13811 
