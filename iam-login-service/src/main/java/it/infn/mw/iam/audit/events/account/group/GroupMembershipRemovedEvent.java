package it.infn.mw.iam.audit.events.account.group;

import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REMOVE_GROUP_MEMBERSHIP;

import java.util.Collection;

import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamGroup;

public class GroupMembershipRemovedEvent extends GroupMembershipUpdatedEvent {

  private static final long serialVersionUID = 1L;

  public GroupMembershipRemovedEvent(Object source, IamAccount account,
      Collection<IamGroup> groups) {
    super(source, account, ACCOUNT_REMOVE_GROUP_MEMBERSHIP, groups);
  }

}
