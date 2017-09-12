## Audit log

IAM Login Service traces interesting, security related events with an audit log.

Audit log messages are marked with the tag `AUDIT` in the Java class field.
An example of audit log is the following:

```console
2017-09-11 09:58:14.560  INFO 13 --- [o-8080-exec-311] AUDIT : {"@type":"IamAuthenticationSuccessEvent","timestamp":1505116694560,"category":"AUTHENTICATION","principal":"794fb313-6e93-4d02-9d0a-4ed773ee2c5e","message":"794fb313-6e93-4d02-9d0a-4ed773ee2c5e authenticated succesfully","sourceEvent":{"principal":"794fb313-6e93-4d02-9d0a-4ed773ee2c5e","type":"InteractiveAuthenticationSuccessEvent"},"source":"UsernamePasswordAuthenticationToken"}
```

Currently the events traced are the following:
- Account:
  * Creation/removal
  * Update of account fields
  * Link/Unlink of external authentication methods
  * Password reset
  * Add/Remove of authorities
- Group:
  * Creation/removal
  * Add/remove user membership
- Authentication
  * Success/failure
- Registration
  * New registration request
  * Confirmation
  * Approval/Rejection of a request


**WARNING: IAM Login Service writes all the log to standard output. So, if
you deploy the service with `systemd`, all the log are collected by the system journal.**
