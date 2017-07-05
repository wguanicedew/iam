package it.infn.mw.iam.audit.events.account.saml;

import java.util.Collection;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.infn.mw.iam.api.scim.updater.UpdaterType;
import it.infn.mw.iam.audit.events.account.AccountUpdatedEvent;
import it.infn.mw.iam.audit.utils.IamSamlSerializer;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamSamlId;

public abstract class SamlAccountUpdatedEvent extends AccountUpdatedEvent {

  private static final long serialVersionUID = 1L;

  @JsonSerialize(using = IamSamlSerializer.class)
  private Collection<IamSamlId> samlIds;

  public SamlAccountUpdatedEvent(Object source, IamAccount account, UpdaterType type,
      Collection<IamSamlId> samlIds) {
    super(source, account, type, buildMessage(type, account, samlIds));
    this.samlIds = samlIds;
  }

  protected Collection<IamSamlId> getSamlIds() {
    return samlIds;
  }

  protected static String buildMessage(UpdaterType t, IamAccount account,
      Collection<IamSamlId> samlIds) {
    return String.format("%s: username: '%s' values '%s'", t.getDescription(),
        account.getUsername(), samlIds);
  }

}
