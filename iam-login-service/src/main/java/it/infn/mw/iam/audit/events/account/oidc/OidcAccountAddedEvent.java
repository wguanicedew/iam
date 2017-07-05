package it.infn.mw.iam.audit.events.account.oidc;

import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_ADD_OIDC_ID;

import java.util.Collection;

import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamOidcId;

public class OidcAccountAddedEvent extends OidcAccountUpdatedEvent {

  private static final long serialVersionUID = 1L;

  public OidcAccountAddedEvent(Object source, IamAccount account, Collection<IamOidcId> oidcIds) {
    super(source, account, ACCOUNT_ADD_OIDC_ID, oidcIds);
  }

}
