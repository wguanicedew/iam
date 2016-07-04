package it.infn.mw.iam.authn.oidc.service;



public interface OidcUserDetailsService {

  Object loadUserByOIDC(String subject, String issuer);

}
