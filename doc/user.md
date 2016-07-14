# User guide

## OpenID-connect Authorization Server

The IAM is based on the [MitreID-connect server application][mitre], so refer
to the [mitre documentation][mitre-doc] for help on client management and other
[APIs][mitre-doc-api] that are exposed by the service.

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

## SCIM API reference

### /Users

### /Groups


[mitre]: https://github.com/mitreid-connect/OpenID-Connect-Java-Spring-Server
[mitre-doc]: https://github.com/mitreid-connect/OpenID-Connect-Java-Spring-Server/wiki
[mitre-doc-api]: https://github.com/mitreid-connect/OpenID-Connect-Java-Spring-Server/wiki/API
