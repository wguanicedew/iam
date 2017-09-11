## Health checks

IAM Login Service expose health endpoints to monitor the running instance status.

Health endpoints expose a different set of information, depending on the
requests is authenticated: users with authority `ROLE_ADMIN` can see more
details; anonymous requests receive only a summary of the health status.

These endpoints return:
- HTTP code 200 if everything is ok;
- HTTP code 500 if any health check fail.

##### `/health`
This endpoint monitors the health application, composing disk space and database
health check.

Examples.
```console
$ curl -s https://iam.local.io/health | jq
{
  "status": "UP"
}
```

Sending basic authentication, the endpoint return a response with more details:
```console
$ curl -s -u $ADMINUSER:$ADMINPASSWORD https://iam.local.io/health | jq
{
  "status": "UP",
  "diskSpace": {
    "status": "UP",
    "total": 10725883904,
    "free": 9872744448,
    "threshold": 10485760
  },
  "db": {
    "status": "UP",
    "database": "MySQL",
    "hello": 1
  }
}
```

##### `/health/mail`
This endpoint monitors the connection to the SMTP server configured for the
IAM Notification Service.

```console
$ curl -s https://iam.local.io/health/mail | jq
{
  "status": "UP"
}
```

With an authenticated request, the SMTP server details are returned:
```console
$ curl -u $ADMINUSER:$ADMINPASSWORD https://iam.local.io/health/mail | jq
{
  "status": "UP",
  "mail": {
    "status": "UP",
    "location": "smtp.local.io:25"
  }
}
```

##### `/health/external`
This endpoint checks the reachability of external servers.
In this IAM release, only Google IDP provider is monitored.

```console
$ curl -s https://iam.local.io/health/external | jq
{
  "status": "UP"
}
```

With an authenticated request, the external service URL is shown in the details.
```console
$ curl -s -u $ADMINUSER:$ADMINPASSWORD https://iam.local.io/health/external | jq
{
  "status": "UP",
  "google": {
    "status": "UP",
    "location": "http://www.google.it"
  }
}
```
