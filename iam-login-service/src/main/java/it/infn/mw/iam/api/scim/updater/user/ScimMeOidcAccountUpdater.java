package it.infn.mw.iam.api.scim.updater.user;

import it.infn.mw.iam.api.scim.exception.ScimPatchOperationNotSupported;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.persistence.model.IamAccount;

public class ScimMeOidcAccountUpdater extends OpenIDConnectAccountUpdater {

  @Override
  public boolean add(IamAccount account, ScimUser user) {
    throw new ScimPatchOperationNotSupported(
        "Add OpenID Connect account not supported on this endpoint");
  }
}
