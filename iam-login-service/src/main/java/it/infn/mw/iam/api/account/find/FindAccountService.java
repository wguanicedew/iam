package it.infn.mw.iam.api.account.find;

import org.springframework.data.domain.Pageable;

import it.infn.mw.iam.api.scim.model.ScimListResponse;
import it.infn.mw.iam.api.scim.model.ScimUser;

public interface FindAccountService {

  ScimListResponse<ScimUser> findAccountByLabel(String labelName, String labelValue, Pageable pageable);

  ScimListResponse<ScimUser> findAccountByEmail(String emailAddress);

  ScimListResponse<ScimUser> findAccountByUsername(String username);

  ScimListResponse<ScimUser> findInactiveAccounts(Pageable pageable);

  ScimListResponse<ScimUser> findActiveAccounts(Pageable pageable);

  ScimListResponse<ScimUser> findAccountByGroupName(String groupName, Pageable pageable);

  ScimListResponse<ScimUser> findAccountByGroupUuid(String groupUuid, Pageable pageable);

}
