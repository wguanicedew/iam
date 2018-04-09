package it.infn.mw.iam.core.expression;

import java.util.Optional;

import org.springframework.security.core.Authentication;

import it.infn.mw.iam.api.account.AccountUtils;
import it.infn.mw.iam.api.requests.GroupRequestUtils;
import it.infn.mw.iam.core.IamGroupRequestStatus;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamGroupRequest;

public class IamSecurityExpressionMethods {

  private final Authentication authentication;
  private final AccountUtils accountUtils;
  private final GroupRequestUtils groupRequestUtils;


  public IamSecurityExpressionMethods(Authentication authentication, AccountUtils accountUtils,
      GroupRequestUtils groupRequestUtils) {
    this.authentication = authentication;
    this.accountUtils = accountUtils;
    this.groupRequestUtils = groupRequestUtils;
  }

  public boolean isGroupManager(String groupUuid) {

    return authentication.getAuthorities()
      .stream()
      .filter(a -> a.getAuthority().equals("ROLE_GM:" + groupUuid))
      .findAny()
      .isPresent();
  }

  public boolean isUser(String userUuid) {
    Optional<IamAccount> account = accountUtils.getAuthenticatedUserAccount();
    return account.isPresent() && account.get().getUuid().equals(userUuid);
  }

  public boolean userOwnsGroupRequest(String requestId) {
    Optional<IamGroupRequest> groupRequest = groupRequestUtils.getGroupRequestUuid(requestId);
    Optional<IamAccount> userAccount = accountUtils.getAuthenticatedUserAccount();

    return userAccount.isPresent() && groupRequest.isPresent()
        && groupRequest.get().getAccount().getUuid().equals(userAccount.get().getUuid());
  }

  public boolean userCanDeleteGroupRequest(String requestId) {
    Optional<IamGroupRequest> groupRequest = groupRequestUtils.getGroupRequestUuid(requestId);

    return groupRequest.isPresent() && userOwnsGroupRequest(requestId)
        && IamGroupRequestStatus.PENDING.equals(groupRequest.get().getStatus());
  }

}
