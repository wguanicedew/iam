package it.infn.mw.iam.api.scim.updater.builders;

import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_ADD_GROUP_MEMBERSHIP;
import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REMOVE_GROUP_MEMBERSHIP;

import java.util.function.Consumer;
import java.util.function.Predicate;

import it.infn.mw.iam.api.scim.updater.AccountUpdater;
import it.infn.mw.iam.api.scim.updater.DefaultAccountUpdater;
import it.infn.mw.iam.audit.events.account.group.GroupMembershipAddedEvent;
import it.infn.mw.iam.audit.events.account.group.GroupMembershipRemovedEvent;
import it.infn.mw.iam.core.user.IamAccountService;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamAccountGroupMembership;
import it.infn.mw.iam.persistence.model.IamGroup;

public class GroupMembershipManagement {



  private final IamAccount account;
  private final IamAccountService accountService;

  private final Consumer<IamGroup> groupAdder;
  private final Consumer<IamGroup> groupRemover;
  private final Predicate<IamGroup> accountIsMember;

  public GroupMembershipManagement(IamAccount account, IamAccountService accountService) {
    this.account = account;
    this.accountService = accountService;

    groupAdder = (group) -> this.accountService.addToGroup(this.account, group);
    groupRemover = (group) -> this.accountService.removeFromGroup(this.account, group);

    accountIsMember = (group) -> {
      for (IamAccountGroupMembership m : this.account.getGroups()) {
        if (m.getGroup().equals(group)) {
          return true;
        }
      }
      return false;
    };

  }

  public AccountUpdater addToGroup(IamGroup group) {
    return new DefaultAccountUpdater<IamGroup, GroupMembershipAddedEvent>(account,
        ACCOUNT_ADD_GROUP_MEMBERSHIP, groupAdder, group, accountIsMember.negate(),
        GroupMembershipAddedEvent::new);

  }

  public AccountUpdater removeFromGroup(IamGroup group) {
    return new DefaultAccountUpdater<IamGroup, GroupMembershipRemovedEvent>(account,
        ACCOUNT_REMOVE_GROUP_MEMBERSHIP, groupRemover, group, accountIsMember,
        GroupMembershipRemovedEvent::new);
  }

}
