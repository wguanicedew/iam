package it.infn.mw.iam.core.oauth.scope;

import java.util.Set;

import org.springframework.security.core.Authentication;

@FunctionalInterface
public interface IamScopeFilter {
  
  public void filterScopes(Set<String> scopes, Authentication authn);

}