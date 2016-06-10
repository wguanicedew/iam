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
		
		throw new ScimPatchOperationNotSupported("Expected 'members' as path value");
	  }
	  
	  switch (op.getOp()) {

	  case add:

		// value cannot be null
		if (op.getValue() == null) {
		  throw new ScimValidationException(
			"Expected patch operation value not null");
		}
		for (ScimMemberRef ref : op.getValue()) {

		  IamAccount account = accountRepository.findByUuid(ref.getValue())
			.orElseThrow(() -> new ScimResourceNotFoundException(
			  "No user mapped to id '" + ref.getValue() + "'"));

		  account.getGroups().add(group);
		  account.touch();

		  accountRepository.save(account);
		}

		break;
	  case remove:
		// value can be null >> remove all
		if (op.getValue() == null) {

		  for (IamAccount account : group.getAccounts()) {

			account.getGroups().remove(group);
			account.touch();

			accountRepository.save(account);
		  }
		} else {

		  for (ScimMemberRef ref : op.getValue()) {

			IamAccount account = accountRepository.findByUuid(ref.getValue())
			  .orElseThrow(() -> new ScimResourceNotFoundException(
				"No user mapped to id '" + ref.getValue() + "'"));

			account.getGroups().remove(group);
			account.touch();

			accountRepository.save(account);
		  }
		}

		break;
	  case replace:
		// ??
		break;
	  default:
		break;
	  }
	}
	return;

  }
  
}