package it.infn.mw.iam.audit.events.account.group;

import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_ADD_GROUP_MEMBERSHIP;

import java.util.Collection;

import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamGroup;

public class GroupMembershipAddedEvent extends GroupMembershipUpdatedEvent {

  private static final long serialVersionUID = 1L;


  public GroupMembershipAddedEvent(Object source, IamAccount account, Collection<IamGroup> groups) {
    super(source, account, ACCOUNT_ADD_GROUP_MEMBERSHIP, groups);
  }

}
