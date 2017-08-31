package it.infn.mw.iam.audit.events.account.oidc;

import java.util.Collection;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.infn.mw.iam.api.scim.updater.UpdaterType;
import it.infn.mw.iam.audit.events.account.AccountUpdatedEvent;
import it.infn.mw.iam.audit.utils.IamOidcSerializer;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamOidcId;

public abstract class OidcAccountUpdatedEvent extends AccountUpdatedEvent {

  private static final long serialVersionUID = 1L;

  @JsonSerialize(using = IamOidcSerializer.class)
  private final Collection<IamOidcId> oidcIds;

  public OidcAccountUpdatedEvent(Object source, IamAccount account, UpdaterType type,
      Collection<IamOidcId> oidcIds) {
    super(source, account, type, buildMessage(type, account, oidcIds));
    this.oidcIds = oidcIds;
  }

  protected Collection<IamOidcId> getOidcIds() {
    return oidcIds;
  }

  protected static String buildMessage(UpdaterType t, IamAccount account,
      Collection<IamOidcId> oidcIds) {
    return String.format("%s: username: '%s' values: '%s'", t.getDescription(),
        account.getUsername(), oidcIds);
  }

}
