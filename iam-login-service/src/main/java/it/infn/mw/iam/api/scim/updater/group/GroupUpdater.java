package it.infn.mw.iam.api.scim.updater.group;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.infn.mw.iam.api.scim.exception.ScimException;
import it.infn.mw.iam.api.scim.exception.ScimResourceNotFoundException;
import it.infn.mw.iam.api.scim.model.ScimMemberRef;
import it.infn.mw.iam.api.scim.updater.Updater;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamGroup;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

@Component
public class GroupUpdater implements Updater<IamGroup, List<ScimMemberRef>> {

  @Autowired
  private IamAccountRepository accountRepository;

  @Override
  public boolean add(IamGroup group, List<ScimMemberRef> members) {

    if (members == null || members.isEmpty()) {
      throw new ScimException("Empty list of members");
    }

    boolean hasChanged = false;
    for (ScimMemberRef member : members) {
      hasChanged |= addMembership(member.getValue(), group);
    }
    return hasChanged;
  }

  @Override
  public boolean remove(IamGroup group, List<ScimMemberRef> members) {

    if (members == null || members.isEmpty()) {
      return removeAllMembers(group);
    }
    for (ScimMemberRef member : members) {
      removeMembership(member.getValue(), group);
    }
    return true;
  }

  @Override
  public boolean replace(IamGroup group, List<ScimMemberRef> members) {

    if (members == null || members.isEmpty()) {
      throw new ScimException("Empty list of members");
    }

    boolean hasChanged = false;

    // update groups of removed members
    for (IamAccount member : group.getAccounts()) {

      boolean found = false;

      for (ScimMemberRef newMember : members) {
        if (newMember.getValue().equals(member.getUuid())) {
          found = true;
        }
      }

      if (!found) {
        removeMembership(member.getUuid(), group);
        hasChanged = true;
      }
    }

    // add new members
    for (ScimMemberRef member : members) {
      hasChanged |= addMembership(member.getValue(), group);
    }

    return hasChanged;
  }

  private boolean removeAllMembers(IamGroup group) {

    if (group.getAccounts().size() == 0) {
      return false;
    }

    for (IamAccount account : group.getAccounts()) {

      account.getGroups().remove(group);
      account.touch();
      accountRepository.save(account);
    }
    return true;
  }

  private boolean addMembership(String uuid, IamGroup group) {

    IamAccount account = accountRepository.findByUuid(uuid)
      .orElseThrow(() -> new ScimResourceNotFoundException("No user mapped to id '" + uuid + "'"));

    if (account.isMemberOf(group)) {
      return false;
    }
    account.getGroups().add(group);
    account.touch();
    accountRepository.save(account);
    return true;
  }

  private void removeMembership(String uuid, IamGroup group) {

    IamAccount account = accountRepository.findByUuid(uuid)
      .orElseThrow(() -> new ScimResourceNotFoundException("No user mapped to id '" + uuid + "'"));

    if (!account.isMemberOf(group)) {
      throw new ScimResourceNotFoundException(
          "User " + account.getUsername() + " is not member of " + group.getName());
    }

    account.getGroups().remove(group);
    account.touch();
    accountRepository.save(account);
  }

  @Override
  public boolean accept(List<ScimMemberRef> updates) {
    return true;
  }
}
