package it.infn.mw.iam.audit.events.account.group;

import java.util.Collection;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.infn.mw.iam.api.scim.updater.UpdaterType;
import it.infn.mw.iam.audit.events.account.AccountUpdatedEvent;
import it.infn.mw.iam.audit.utils.IamGroupCollectionSerializer;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamGroup;

public abstract class GroupMembershipUpdatedEvent extends AccountUpdatedEvent {

  private static final long serialVersionUID = 1L;

  @JsonSerialize(using = IamGroupCollectionSerializer.class)
  private final Collection<IamGroup> groups;

  public GroupMembershipUpdatedEvent(Object source, IamAccount account, UpdaterType type,
      Collection<IamGroup> groups) {
    super(source, account, type, buildMessage(type, account, groups));
    this.groups = groups;
  }

  protected Collection<IamGroup> getGroups() {
    return groups;
  }

  protected static String buildMessage(UpdaterType t, IamAccount account,
      Collection<IamGroup> groups) {
    return String.format("%s: username: '%s' values: '%s'", t.getDescription(),
        account.getUsername(), groups);
  }

}
