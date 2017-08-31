package it.infn.mw.iam.authn.oidc.service;

import org.mitre.openid.connect.model.OIDCAuthenticationToken;
@FunctionalInterface
public interface OidcUserDetailsService {

  Object loadUserByOIDC(OIDCAuthenticationToken token);


}
