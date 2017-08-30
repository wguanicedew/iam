package it.infn.mw.iam.core.oauth.scope;

import java.util.Set;

import org.springframework.security.oauth2.provider.OAuth2Authentication;

@FunctionalInterface
public interface IamScopeFilter {
  
  public void filterScopes(Set<String> scopes, OAuth2Authentication authn);

}