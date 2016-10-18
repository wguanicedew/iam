## OAuthN Token exchange support

The current release of Indigo IAM implements an essential set of feature of the
[Token Exchange OAuth specification](https://tools.ietf.org/html/draft-ietf-oauth-token-exchange-05).
Indigo IAM supports only impersonation semantic: delegation is not yet supported.

### Configuration
Clients that request Token Exchange must be configured with
the `urn:ietf:params:oauth:grant-type:token-exchange` grant type enabled.

### Usage
A client who wants to exchange an access token with a new one, must send a request to the `/token` endpoint,
with the properties described below:

1. Supply its valid access token in `subject_token` request field.
2. Specify a valid client_id in `audience` field: this is the ID of the target resource which client wants to access.
3. Requested scopes must be a subset of the scopes enabled on the client specified as `audience`.

There are some scopes that are considered "specials".
They are identity, refresh and SCIM related scopes:

`openid`, `profile`, `email`, `address`, `phone`, `offline_access`, `scim:read`, `scim:write`

These scopes, in order to be "exchanged" across services, need to be present in the set of
scopes linked to the subject token that is presented for the exchange.


### Limitation and known issues
The current implementation of Token Exchange in Indigo IAM, has some limitations.

 * Delegation is not yet supported: if `actor_token` or the flag `want_composite` are specified within the request, an error
 response is returned by the authorization server.
 * The `audience` field is mandatory and not optional, as mentioned into the specification: it must be a valid client identifier.
 * The `resource` field is ignored.

 
### Token Exchange example
This section describe a typical scenario of token exchange flow.

A client requests to the Authorization Service (AS) an access token (T1) to access a protected resource (PR).
In this example, we use a registered client with `client_credentials` grant type to obtain the token and `curl` to send the HTTP request.

```bash
 $ export IAM_TOKEN_ENDPOINT=https://iam.local.io/token
 $ export CLIENT_ID=token-exchange-subject
 $ export CLIENT_SECRET=secret

 $ curl -sH "Content-Type: application/x-www-form-urlencoded" \
    --data "client_id=$CLIENT_ID&client_secret=$CLIENT_SECRET&grant_type=client_credentials&scope=openid%20profile" \
    $IAM_TOKEN_ENDPOINT | tee /tmp/response | jq
```
This request returns the following JSON, containing a new access token:

```
 {
    "access_token": "eyJraWQiOiJyc2ExIiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJpYW0tY2xpZW50IiwiaXNzIjoiaHR0cHM6XC9cL2lhbS10ZXN0LmluZGlnby1kYXRhY2xvdWQuZXVcLyIsImV4cCI6MTQ3Njc5NzYyMCwiaWF0IjoxNDc2Nzk0MDIwLCJqdGkiOiI3MTVlZTA4My1kYWZjLTRjYTItODc0ZC0zZTU5Y2MzZjVjMDEifQ.N81Smuu9Ua0YQsMA7r71-IibjVi1ZRtj1pS9zRcOi-VwaRZyhwsH4FOtZOLAHGQh_7RYX_6746Gaoma-5eoETMv2Z2pPq76Pxf32u_TGk_P6SEZEVpk001IeUlE3TFpbYErO62bFloL7MBFGdcl8AQLz9EW6mWw4s3fffO8-whQ",
    "token_type": "Bearer",
    "expires_in": 3599,
    "scope": "openid profile"
 }
```

To simplify next steps, we can extract the access token into an environment variable:

```
 $ export ACCESS_TOKEN_T1=`cat /tmp/response | jq .access_token | tr -d '"'`
```
Now the client can access the protected resource PR1, supplying this access token. 
For example:

```
curl -s --get -H "Authorization: Bearer $ACCESS_TOKEN_T1" http://pr1.example.org/api
```

Then, PR1 needs access to a resource on another protected resource (PR2).

In Token Exchange terms, the client is the `subject`, PR1 acts as `actor` and PR2 is the `audience`.
So PR1 goes to the Authorization Service to exchange access token T1 with another access token (T2).

In the Token Exchange request, PR1 specify the following parameters:

| Parameter          | Value |
|--------------------|-------|
| grant_type         | `urn:ietf:params:oauth:grant-type:token-exchange` |
| subject_token      | The access token supplied by the client. In this example T1 | 
| subject_token_type | `urn:ietf:params:oauth:token-type:access_token` or `urn:ietf:params:oauth:token-type:jwt` |
| scope              | Space separated list of scopes  desired |
| audience           | A valid client id |

```
$ export ACTOR_ID=token-exchange-actor
$ export ACTOR_SECRET=secret
$ export AUDIENCE=task-app

$ curl -s -u $ACTOR_ID:$ACTOR_SECRET \
	-H "Content-Type: application/x-www-form-urlencoded" \
    --data "grant_type=urn%3Aietf%3Aparams%3Aoauth%3Agrant-type%3Atoken-exchange&audience=$AUDIENCE%20&subject_token=$ACCESS_TOKEN_T1&subject_token_type=urn%3Aietf%3Aparams%3Aoauth%3Atoken-type%3Aaccess_token&scope=read-tasks" \
    $IAM_TOKEN_ENDPOINT | tee /tmp/response | jq

```
The response is like the following:

```
{
  "access_token": "eyJraWQiOiJyc2ExIiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJ0b2tlbi1leGNoYW5nZS1hY3RvciIsImlzcyI6Imh0dHBzOlwvXC9pYW0ubG9jYWwuaW9cLyIsImV4cCI6MTQ3NjgwMTIyNiwiaWF0IjoxNDc2Nzk3NjI2LCJqdGkiOiIyNmY4YjJmZS1mNGU1LTQ0NTgtYTI2Yi1jOWY2MDNmMTgxODEifQ.edb6gaT5m56z76g_BTFfYQD95lgJf50vAZd0k9UDiS-1mJu0pluX_3Ry_rJjdDQdDc1T14XXKGeYRE8NLmZTolWclt5Wf_G9hWZutFBokb1kejG6GDhzY4fWZE43A5SEBhlEAfUFsotCnFWjRqy93AZZej-BCNA9KlREh0aOin0",
  "token_type": "Bearer",
  "expires_in": 3599,
  "scope": "read-tasks",
  "issued_token_type": "urn:ietf:params:oauth:token-type:access_token"
}
```
We can save the new token into an environment variable, again:

```
$ export ACCESS_TOKEN_T2=`cat /tmp/response | jq .access_token | tr -d '"'`
```

Now, PR1, impersonating the user client, can access PR2 with the new token. 
For example:

```
$ curl -s --get -H "Authorization: Bearer $ACCESS_TOKEN_T2" http://task-app.example.org/api
```

