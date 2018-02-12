package it.infn.mw.iam.core.expression;

import java.util.Optional;

import org.springframework.security.core.Authentication;

import it.infn.mw.iam.api.account.AccountUtils;
import it.infn.mw.iam.persistence.model.IamAccount;

public class IamSecurityExpressionMethods {

  private final Authentication authentication;
  private final AccountUtils accountUtils;

  public IamSecurityExpressionMethods(Authentication authentication, AccountUtils accountUtils) {
    this.authentication = authentication;
    this.accountUtils = accountUtils;
  }

  public boolean isGroupManager(String groupUuid) {

    return authentication.getAuthorities()
      .stream()
      .filter(a -> a.getAuthority().equals("ROLE_GM:" + groupUuid))
      .findAny()
      .isPresent();
  }

  public boolean isUser(String uuid) {
    Optional<IamAccount> account = accountUtils.getAuthenticatedUserAccount();
    return account.isPresent() && account.get().getUuid().equals(uuid);
  }

}
