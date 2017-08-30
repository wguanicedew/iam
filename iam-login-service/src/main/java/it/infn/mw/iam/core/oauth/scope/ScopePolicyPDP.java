package it.infn.mw.iam.core.oauth.scope;

import java.util.Set;

import it.infn.mw.iam.persistence.model.IamAccount;

@FunctionalInterface
public interface ScopePolicyPDP {

  Set<String> filterScopes(Set<String> requestedScopes, IamAccount account);

}
