package it.infn.mw.iam.authn.oidc;

import org.springframework.util.MultiValueMap;

import it.infn.mw.iam.authn.oidc.OidcClientFilter.OidcProviderConfiguration;
@FunctionalInterface
public interface OidcTokenRequestor {

  String requestTokens(OidcProviderConfiguration conf,
      MultiValueMap<String, String> tokenRequestParams) throws OidcClientError;

}
