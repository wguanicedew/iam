package it.infn.mw.iam.api.scim.provisioning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import it.infn.mw.iam.api.scim.converter.GroupConverter;
import it.infn.mw.iam.api.scim.exception.IllegalArgumentException;
import it.infn.mw.iam.api.scim.exception.ResourceNotFoundException;
import it.infn.mw.iam.api.scim.model.ScimGroup;
import it.infn.mw.iam.api.scim.model.ScimListResponse;
import it.infn.mw.iam.api.scim.provisioning.paging.OffsetPageable;
import it.infn.mw.iam.api.scim.provisioning.paging.ScimPageRequest;
import it.infn.mw.iam.persistence.model.IamAccount;
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
	
	Date creationTime = new Date();

	iamGroup.setUuid(group.getId());
	iamGroup.setName(group.getDisplayName());
	iamGroup.setCreationTime(creationTime);
	iamGroup.setLastUpdateTime(creationTime);
	iamGroup.setAccounts(new HashSet<IamAccount>());
	
	groupRepository.save(iamGroup);

	return converter.toScim(iamGroup);
  }

  @Override
  public void delete(String id) {

	idSanityChecks(id);
	
	IamGroup group = groupRepository.findByUuid(id)
	  .orElseThrow(() -> new ResourceNotFoundException(
		"No group mapped to id '" + id + "'"));

	groupRepository.delete(group);
  }

  @Override
  public ScimGroup replace(String id, ScimGroup scimItemToBeUpdated) {

	IamGroup existingGroup = groupRepository.findByUuid(id)
      .orElseThrow(() -> new ResourceNotFoundException(
        "No group mapped to id '" + id + "'"));

	if (groupRepository
	  .findByNameWithDifferentId(scimItemToBeUpdated.getDisplayName(), id)
	  .isPresent()) {
	  throw new IllegalArgumentException(
		"displayName is already mappped to another group");
	}

    IamGroup updatedGroup = converter.fromScim(scimItemToBeUpdated);

    updatedGroup.setId(existingGroup.getId());
    updatedGroup.setUuid(existingGroup.getUuid());
    updatedGroup.setCreationTime(existingGroup.getCreationTime());

    updatedGroup.touch();
    
    groupRepository.save(updatedGroup);
    return converter.toScim(updatedGroup);
  }

  @Override
  public ScimListResponse<ScimGroup> list(ScimPageRequest params) {

	if (params.getCount() == 0) {
      int groupCount = groupRepository.countAllGroups();
      return new ScimListResponse<>(Collections.emptyList(), groupCount, 0, 1);
    }

    OffsetPageable op = new OffsetPageable(params.getStartIndex(),
      params.getCount());

    Page<IamGroup> results = groupRepository.findAll(op);

    List<ScimGroup> resources = new ArrayList<>();

    results.getContent()
      .forEach(g -> resources.add(converter.toScim(g)));

    return new ScimListResponse<>(resources, results.getTotalElements(),
      resources.size(), op.getOffset() + 1);
  }

}
