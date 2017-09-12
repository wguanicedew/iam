## Health checks

The IAM Login Service exposes a set of health endpoints that can be used to
monitor the status of the service.

Health endpoints expose a different set of information depending on the user
privileges; administrator users can see more details, while anonymous requests
typically receive only a summary of the health status.

These endpoints return:
- HTTP code 200 if everything is ok;
- HTTP code 500 if any health check fails.

##### `/health`
This is a general application health check endpoint, which composes disk space
and database health checks.

Examples.
```console
$ curl -s https://iam.local.io/health | jq
{
  "status": "UP"
}
```

Sending basic authentication, the endpoint returns a response with more details:
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
By default, the endpoint triggers a check on the connectivity to Google.

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
