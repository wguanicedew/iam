## OAuth Token exchange support

The current release of Indigo IAM implements part of the
[Token Exchange OAuth specification](https://tools.ietf.org/html/draft-ietf-oauth-token-exchange-05).
This specification defines two semantics: impersonation and delegation.

When a subject A impersonate B, A has all the rights of B and it is indistinguishable from B.
So, when A interacts within any other entity, A is B.

With delegation A still has its own identity, separate from B.
So when A interacts within another entity, it is explicit that A is representing B,
because B has delegated some of its rights to A.  

More details about the difference from this two semantics can be found in 
[this section of the specification](https://tools.ietf.org/html/draft-ietf-oauth-token-exchange-05#section-1.1).

Indigo IAM supports only impersonation semantic: delegation is not yet supported.


### Configuration
Clients that request Token Exchange must be configured with
the `urn:ietf:params:oauth:grant-type:token-exchange` grant type enabled.
This grant type can't be enabled with dynamic registered clients: only users with `ROLE_ADMIN` privileges
can assign this grant to the clients.


### Usage
A client who wants to exchange an access token with a new one (or a couple of new tokens, in case a refresh token is requested), 
must send a request to the `/token` endpoint, specifying the following properties:

1. Provide a valid IAM access token in the `subject_token` request field.
2. Specify a set of scopes requested for the new token using the `scope` parameter; 
note that the set of scope must be a subset of the scopes enabled for both the subject and the actor.

Some scopes, called _"Internal"_ scopes, are handled in a special way. These scopes are:

`openid`, `profile`, `email`, `address`, `phone`, `offline_access`, `scim:read`, `scim:write`, `registration:read`, `registration:write`

These scopes, in order to be "exchanged" across services, need to be present in the set of
scopes linked to the subject token that is presented for the exchange.


### Limitation and known issues
The current implementation of Token Exchange in Indigo IAM has some limitations.

 * Delegation is not yet supported: if `actor_token` or the flag `want_composite` are specified within the request, an error
 response is returned by the authorization server.
 * The `resource` field is ignored.
 * The `audience` field is optional: its value is not validated by the IAM.

 
### Token Exchange example
This section describe a typical scenario of token exchange flow.

A client requests to the Authorization Service (AS) an access token (T1) to access a protected resource (PR).
In this example, we use a registered client with `password` grant type to obtain the token and `curl` to send the HTTP request.

```bash
 $ export IAM_TOKEN_ENDPOINT=https://iam.local.io/token
 $ export CLIENT_ID=token-exchange-subject
 $ export CLIENT_SECRET=secret
 $ export USERNAME=my_username
 $ export PASSWORD=my_secret_password

 $ curl -s -u $CLIENT_ID:$CLIENT_SECRET \
    -d username=$USERNAME \
    -d password=$PASSWORD \
    -d grant_type=password \
    -d scope="openid profile" \
    $IAM_TOKEN_ENDPOINT | tee /tmp/response | jq
```
This request returns a JSON containing a new access token, like the following:

```
{
  "access_token": "eyJraWQiOiJyc2ExIiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJhY2JjY2QwOC1kNzNkLTQxZjItODk3MS1iNjA4ZmNjNjYyNmQiLCJpc3MiOiJodHRwczpcL1wvaWFtLmxvY2FsLmlvXC8iLCJleHAiOjE0NzY5NTcxMDIsImlhdCI6MTQ3Njk1MzUwMiwianRpIjoiMjBiZThlNjYtNmNmOS00YzE0LWI4ZDEtZjJmZTc0NDk0YjAxIn0.kqAhZ2MNmBLYIA_-xW9356kD-ndqJ7jKUZRPb7ox_4iXbjcnV6oZYAHZzTH_uBTXA2WsVIJJ-Qicm5JQ0ydb2ewgECAmGkKfL3X4qnnRq2_GgZZof3zlM_rIz3QrDB3v1eIt42YeMdUgODUYGKeDwntT5a7wPDtxe-GM2uL5fik",
  "token_type": "Bearer",
  "expires_in": 3599,
  "scope": "openid profile",
  "id_token": "eyJraWQiOiJyc2ExIiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJhY2JjY2QwOC1kNzNkLTQxZjItODk3MS1iNjA4ZmNjNjYyNmQiLCJhdWQiOiIzMmMzMTUyOS05YmM2LTQ1ZWQtYjU0YS0wNGEyNThiMDRmYmYiLCJraWQiOiJyc2ExIiwiaXNzIjoiaHR0cHM6XC9cL2lhbS5sb2NhbC5pb1wvIiwiZXhwIjoxNDc2OTU0MTAyLCJpYXQiOjE0NzY5NTM1MDIsImp0aSI6IjhhMzk1OTM5LTM1N2QtNGY5My04MmEzLTJkMTBkM2ZhMzgzZCJ9.DtNR-ob8kMIUMa2x6TW7krSYMt78tfr5fnTK4aeoIY-wmEWcjPRx1_vT6_lesjMr9w0B_OCALXfOoDBfbF7DhmV7vpbotirkMxvowFBzgppmtBTZNAzLc_Wiwr4IAiGydwjy_UbYrxx6qlWJAKRwzSbDDd3oDVpU-KM8gtLIEa8"
}
```

To simplify next steps, we can extract the access token into an environment variable:

```
 $ export ACCESS_TOKEN_T1=`cat /tmp/response | jq -r .access_token`
```
Now the client can access the protected resource PR1, supplying this access token. 
For example:

```
curl -s --get -H "Authorization: Bearer $ACCESS_TOKEN_T1" http://pr1.example.org/api
```

Then, PR1 needs access to a resource on another protected resource (PR2).

In Token Exchange terms, the client is the `subject` and PR1 acts as `actor` and PR2 can be the `audience`.
So PR1 goes to the Authorization Service to exchange access token T1 with another access token (T2).

In the Token Exchange request, PR1 specify the following parameters:

| Parameter          | Value |
|--------------------|-------|
| grant_type         | `urn:ietf:params:oauth:grant-type:token-exchange` |
| subject_token      | The access token supplied by the client. In this example T1 | 
| subject_token_type | `urn:ietf:params:oauth:token-type:access_token` or `urn:ietf:params:oauth:token-type:jwt` |
| scope              | Space separated list of scopes  desired |
| audience           | Optional. An identifier of the resource where the actor intends to use the token |

```
$ export ACTOR_ID=token-exchange-actor
$ export ACTOR_SECRET=secret
$ export AUDIENCE=tasks-app

$ curl -s -u $ACTOR_ID:$ACTOR_SECRET \
    -d grant_type=urn:ietf:params:oauth:grant-type:token-exchange \
    -d audience=$AUDIENCE \
    -d subject_token=$ACCESS_TOKEN_T1 \
    -d subject_token_type=urn:ietf:params:oauth:token-type:access_token \
    -d scope="read-tasks" \
    $IAM_TOKEN_ENDPOINT | tee /tmp/response | jq

```
The response is like the following:

```
{
  "access_token": "eyJraWQiOiJyc2ExIiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJhY2JjY2QwOC1kNzNkLTQxZjItODk3MS1iNjA4ZmNjNjYyNmQiLCJpc3MiOiJodHRwczpcL1wvaWFtLmxvY2FsLmlvXC8iLCJleHAiOjE0NzY5NTc1NDQsImlhdCI6MTQ3Njk1Mzk0NCwianRpIjoiZmUwZjM1ODAtYzE5ZC00ZmMzLWIyMzMtY2M4N2QxZTdhOGEyIn0.Hld_I4m5CAXbKKUkwGVJ_ZzANM2mjNAx7AIirVTmF4LiNf5e38Pr0Mh7-etfv86VzuX-l5iPlFQmRItC20WwjlTEqWDIn8y7PoMC2biVjSuUWbxcH_wX8EMtg1IjZZASeWp5cyiJjth9GgggXY8qqEccJlE5QkZk9ci56JX7Ahs",
  "token_type": "Bearer",
  "expires_in": 3599,
  "scope": "read-tasks",
  "audience": "tasks-app",
  "issued_token_type": "urn:ietf:params:oauth:token-type:jwt"
}

```
We can save the new token into an environment variable, again:

```
$ export ACCESS_TOKEN_T2=`cat /tmp/response | jq -r .access_token`
```

Now, PR1, impersonating the user client, can access PR2 with the new token. 
For example:

```
$ curl -s --get -H "Authorization: Bearer $ACCESS_TOKEN_T2" http://task-app.example.org/api
```

