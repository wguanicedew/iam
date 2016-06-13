package it.infn.mw.iam.api.scim.updater;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.infn.mw.iam.api.scim.exception.ScimPatchOperationNotSupported;
import it.infn.mw.iam.api.scim.exception.ScimResourceNotFoundException;
import it.infn.mw.iam.api.scim.exception.ScimValidationException;
import it.infn.mw.iam.api.scim.model.ScimMemberRef;
import it.infn.mw.iam.api.scim.model.ScimPatchOperation;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamGroup;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

@Component
public class GroupUpdater implements Updater<IamGroup, List<ScimMemberRef>> {

  private final IamAccountRepository accountRepository;

  @Autowired
  public GroupUpdater(IamAccountRepository accountRepository) {

	this.accountRepository = accountRepository;
  }

  public void update(IamGroup group,
	List<ScimPatchOperation<List<ScimMemberRef>>> operations) {

	for (ScimPatchOperation<List<ScimMemberRef>> op : operations) {

	  if (!op.getPath().equals("members")) {

		throw new ScimPatchOperationNotSupported(
		  "Expected 'members' as path value");
	  }

	  switch (op.getOp()) {

	  case add:

		// value cannot be null
		if (op.getValue() == null) {
		  throw new ScimValidationException(
			"Expected patch operation value not null");
		}

		for (ScimMemberRef ref : op.getValue()) {
		  addMembership(ref.getValue(), group);
		}
		break;
	  case remove:

		// value can be null >> remove all
		if (op.getValue() == null) {
		  removeAllMembers(group);
		  break;
		} 
		
		for (ScimMemberRef ref : op.getValue()) {
			removeMembership(ref.getValue(), group);
		}
		break;
	  case replace:
		
		// value cannot be null
		if (op.getValue() == null) {
		  throw new ScimValidationException(
			"Expected patch operation value not null");
		}

		// replace step 1: clean all memberships
		removeAllMembers(group);
		for (ScimMemberRef ref : op.getValue()) {
		  //replace step 2: add new members
		  addMembership(ref.getValue(), group);
		}
		break;
	  default:
		break;
	  }
	}
	return;

  }

  private void removeAllMembers(IamGroup group) {

	for (IamAccount account : group.getAccounts()) {

	  account.getGroups().remove(group);
	  account.touch();

	  accountRepository.save(account);
	}
  }

  private void addMembership(String uuid, IamGroup group) {

	IamAccount account = accountRepository.findByUuid(uuid)
	  .orElseThrow(() -> new ScimResourceNotFoundException(
		"No user mapped to id '" + uuid + "'"));

	account.getGroups().add(group);
	account.touch();

	accountRepository.save(account);

  }

  private void removeMembership(String uuid, IamGroup group) {

	IamAccount account = accountRepository.findByUuid(uuid)
	  .orElseThrow(() -> new ScimResourceNotFoundException(
		"No user mapped to id '" + uuid + "'"));

	account.getGroups().remove(group);
	account.touch();

	accountRepository.save(account);

  }
}