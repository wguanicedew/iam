package it.infn.mw.iam.core.oauth.scope;

import java.util.Set;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name="iam.enableScopeAuthz", havingValue="false", matchIfMissing=true)
public class IamNullScopeFilter implements IamScopeFilter {

  public IamNullScopeFilter() {
    // empty constructor
  }

  @Override
  public void filterScopes(Set<String> scopes, Authentication authn) {
    // do nothing
  }

}
