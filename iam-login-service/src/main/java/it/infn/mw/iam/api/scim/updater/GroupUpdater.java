package it.infn.mw.iam.api.scim.updater;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.infn.mw.iam.api.scim.exception.ScimPatchOperationNotSupported;
import it.infn.mw.iam.api.scim.exception.ScimResourceNotFoundException;
import it.infn.mw.iam.api.scim.model.ScimMemberRef;
import it.infn.mw.iam.api.scim.model.ScimPatchOperation;
import it.infn.mw.iam.api.scim.model.ScimPatchOperationMembers;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamGroup;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

@Component
public class GroupUpdater implements Updater<IamGroup> {

  private final IamAccountRepository accountRepository;

  @Autowired
  public GroupUpdater(IamAccountRepository accountRepository) {

	this.accountRepository = accountRepository;
  }

  public void update(IamGroup group,
	List<? extends ScimPatchOperation> operations) {

	for (ScimPatchOperation patchOp : operations) {

	  if (!(patchOp instanceof ScimPatchOperationMembers)) {

		throw new ScimPatchOperationNotSupported(
		  "Patch operation class not supported: "
			+ patchOp.getClass().getName());
	  }
	  
	  ScimPatchOperationMembers op = (ScimPatchOperationMembers) patchOp;

	  for (ScimMemberRef ref : op.getValue()) {

		IamAccount account = accountRepository.findByUuid(ref.getValue())
		  .orElseThrow(() -> new ScimResourceNotFoundException(
			"No user mapped to id '" + ref.getValue() + "'"));

		switch (op.getOp()) {
		case add:
		  account.getGroups().add(group);
		  break;
		case remove:
		  account.getGroups().remove(group);
		  break;
		case replace:
		  account.getGroups().add(group);
		  break;
		default:
		  break;
		}
		account.touch();
		
		accountRepository.save(account);
	  }
	}
	return;

  }

}