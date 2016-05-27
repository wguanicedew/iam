package it.infn.mw.iam.api.scim.provisioning;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.infn.mw.iam.api.scim.converter.GroupConverter;
import it.infn.mw.iam.api.scim.exception.IllegalArgumentException;
import it.infn.mw.iam.api.scim.exception.ResourceNotFoundException;
import it.infn.mw.iam.api.scim.model.ScimGroup;
import it.infn.mw.iam.api.scim.model.ScimListResponse;
import it.infn.mw.iam.api.scim.provisioning.paging.ScimPageRequest;
import it.infn.mw.iam.persistence.model.IamGroup;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.persistence.repository.IamGroupRepository;

@Service
public class ScimGroupProvisioning implements ScimProvisioning<ScimGroup> {

  private final GroupConverter converter;
  private final IamGroupRepository groupRepository;

  @Autowired
  public ScimGroupProvisioning(GroupConverter converter,
	IamGroupRepository groupRepository,
	IamAccountRepository accountRepository) {
	
	this.converter = converter;
	this.groupRepository = groupRepository;
  }

  private void idSanityChecks(String id) {

	if (id == null) {
	  throw new IllegalArgumentException("id cannot be null");
	}

	if (id.trim().isEmpty()) {
	  throw new IllegalArgumentException("id cannot be the empty string");
	}
  }

  @Override
  public ScimGroup getById(String id) {

	idSanityChecks(id);

	Optional<IamGroup> group = groupRepository.findByUuid(id);

	if (group.isPresent()) {
	  return converter.toScim(group.get());
	}

	throw new ResourceNotFoundException("No group mapped to id '" + id + "'");
  }

  @Override
  public ScimGroup create(ScimGroup group) {

	IamGroup iamGroup = new IamGroup();

	String uuid = UUID.randomUUID().toString();

	iamGroup.setUuid(uuid);
	iamGroup.setName(group.getDisplayName());
	
	groupRepository.save(iamGroup);

	return converter.toScim(iamGroup);
  }

  @Override
  public void delete(String id) {

	groupRepository.findByUuid(id).ifPresent(a -> {
	  groupRepository.delete(a);
	});
  }

  @Override
  public ScimGroup replace(String id, ScimGroup scimItemToBeUpdated) {

	// TODO Auto-generated method stub
	return null;
  }

  @Override
  public ScimListResponse<ScimGroup> list(ScimPageRequest params) {

	// TODO Auto-generated method stub
	return null;
  }

}
