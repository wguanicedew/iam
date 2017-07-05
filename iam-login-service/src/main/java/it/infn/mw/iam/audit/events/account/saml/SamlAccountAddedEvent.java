package it.infn.mw.iam.audit.events.account.saml;

import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_ADD_SAML_ID;

import java.util.Collection;

import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamSamlId;

public class SamlAccountAddedEvent extends SamlAccountUpdatedEvent {

  private static final long serialVersionUID = 1L;

  public SamlAccountAddedEvent(Object source, IamAccount account, Collection<IamSamlId> samlIds) {
    super(source, account, ACCOUNT_ADD_SAML_ID, samlIds);
  }
}
