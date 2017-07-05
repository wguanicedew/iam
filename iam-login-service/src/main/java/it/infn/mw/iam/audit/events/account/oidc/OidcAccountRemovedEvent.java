package it.infn.mw.iam.audit.events.account.oidc;

import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REMOVE_OIDC_ID;

import java.util.Collection;

import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamOidcId;

public class OidcAccountRemovedEvent extends OidcAccountUpdatedEvent {

  private static final long serialVersionUID = 1L;

  public OidcAccountRemovedEvent(Object source, IamAccount account, Collection<IamOidcId> oidcIds) {
    super(source, account, ACCOUNT_REMOVE_OIDC_ID, oidcIds);
  }

}
