package it.infn.mw.iam.api.scim.provisioning;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

import it.infn.mw.iam.api.scim.converter.GroupConverter;
import it.infn.mw.iam.api.scim.exception.IllegalArgumentException;
import it.infn.mw.iam.api.scim.exception.ScimException;
import it.infn.mw.iam.api.scim.exception.ScimPatchOperationNotSupported;
import it.infn.mw.iam.api.scim.exception.ScimResourceExistsException;
import it.infn.mw.iam.api.scim.exception.ScimResourceNotFoundException;
import it.infn.mw.iam.api.scim.model.ScimGroup;
import it.infn.mw.iam.api.scim.model.ScimListResponse;
import it.infn.mw.iam.api.scim.model.ScimMemberRef;
import it.infn.mw.iam.api.scim.model.ScimPatchOperation;
import it.infn.mw.iam.api.scim.provisioning.paging.OffsetPageable;
import it.infn.mw.iam.api.scim.provisioning.paging.ScimPageRequest;
import it.infn.mw.iam.api.scim.updater.AccountUpdater;
import it.infn.mw.iam.api.scim.updater.factory.DefaultGroupMembershipUpdaterFactory;
import it.infn.mw.iam.audit.events.group.GroupCreatedEvent;
import it.infn.mw.iam.audit.events.group.GroupRemovedEvent;
import it.infn.mw.iam.audit.events.group.GroupReplacedEvent;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamGroup;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.persistence.repository.IamGroupRepository;

@Service
public class ScimGroupProvisioning
    implements ScimProvisioning<ScimGroup, List<ScimMemberRef>>, ApplicationEventPublisherAware {

  private static final int GROUP_NAME_MAX_LENGTH = 50;
  private static final int GROUP_FULLNAME_MAX_LENGTH = 512;

  private final IamGroupRepository groupRepository;

  private final IamAccountRepository accountRepository;

  private final GroupConverter converter;

  private final DefaultGroupMembershipUpdaterFactory groupUpdaterFactory;

  private ApplicationEventPublisher eventPublisher;

  @Autowired
  public ScimGroupProvisioning(IamGroupRepository groupRepository,
      IamAccountRepository accountRepository, GroupConverter converter) {

    this.accountRepository = accountRepository;
    this.groupRepository = groupRepository;
    this.converter = converter;
    this.groupUpdaterFactory = new DefaultGroupMembershipUpdaterFactory(accountRepository);

  }

  public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
    this.eventPublisher = publisher;
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

    IamGroup group = groupRepository.findByUuid(id)
      .orElseThrow(() -> new ScimResourceNotFoundException("No group mapped to id '" + id + "'"));

    return converter.toScim(group);
  }

  @Override
  public ScimGroup create(ScimGroup group) {

    displayNameSanityChecks(group.getDisplayName());

    IamGroup iamGroup = new IamGroup();

    Date creationTime = new Date();
    String uuid = UUID.randomUUID().toString();

    iamGroup.setUuid(uuid);
    iamGroup.setName(group.getDisplayName());
    iamGroup.setCreationTime(creationTime);
    iamGroup.setLastUpdateTime(creationTime);
    iamGroup.setAccounts(new HashSet<>());
    iamGroup.setChildrenGroups(new HashSet<>());

    IamGroup iamParentGroup = null;

    if (group.getIndigoGroup().getParentGroup() != null) {
      String parentGroupUuid = group.getIndigoGroup().getParentGroup().getValue();
      String parentGroupName = group.getIndigoGroup().getParentGroup().getDisplay();

      iamParentGroup = groupRepository.findByUuid(parentGroupUuid)
        .orElseThrow(() -> new ScimResourceNotFoundException(
            String.format("Parent group '%s' not found", parentGroupUuid)));

      String fullName = String.format("%s/%s", parentGroupName, group.getDisplayName());
      fullNameSanityChecks(fullName);

      iamGroup.setParentGroup(iamParentGroup);
      iamGroup.setName(fullName);

      Set<IamGroup> children = iamParentGroup.getChildrenGroups();
      children.add(iamGroup);
    }

    groupRepository.save(iamGroup);
    
    if (iamParentGroup != null) {
      groupRepository.save(iamParentGroup);
    }
    
    eventPublisher.publishEvent(
        new GroupCreatedEvent(this, iamGroup, "Group created with name " + iamGroup.getName()));

    return converter.toScim(iamGroup);
  }

  @Override
  public void delete(String id) {

    idSanityChecks(id);

    IamGroup group = groupRepository.findByUuid(id)
      .orElseThrow(() -> new ScimResourceNotFoundException("No group mapped to id '" + id + "'"));

    if (!(group.getAccounts().isEmpty() && group.getChildrenGroups().isEmpty())) {

      throw new ScimException("Group is not empty");
    }

    IamGroup parent = group.getParentGroup();
    if (parent != null) {
      parent.getChildrenGroups().remove(group);

      groupRepository.save(parent);
    }

    groupRepository.delete(group);

    eventPublisher.publishEvent(new GroupRemovedEvent(this, group,
        String.format("Group %s has been removed", group.getName())));
  }

  @Override
  public ScimGroup replace(String id, ScimGroup scimItemToBeReplaced) {

    IamGroup existingGroup = groupRepository.findByUuid(id)
      .orElseThrow(() -> new ScimResourceNotFoundException("No group mapped to id '" + id + "'"));

    /* displayname is required */
    String displayName = scimItemToBeReplaced.getDisplayName();

    if (!isGroupNameAvailable(displayName, id)) {
      throw new ScimResourceExistsException(displayName + " is already mappped to another group");
    }

    IamGroup updatedGroup = converter.fromScim(scimItemToBeReplaced);
    /* SCIM resource identifiers cannot be replaced by PUT */
    updatedGroup.setId(existingGroup.getId());
    updatedGroup.setUuid(existingGroup.getUuid());
    updatedGroup.setCreationTime(existingGroup.getCreationTime());
    updatedGroup.setAccounts(existingGroup.getAccounts());
    /* description is not mapped into SCIM group */
    updatedGroup.setDescription(existingGroup.getDescription());
    updatedGroup.setParentGroup(existingGroup.getParentGroup());
    updatedGroup.setChildrenGroups(existingGroup.getChildrenGroups());

    updatedGroup.touch();

    groupRepository.save(updatedGroup);

    eventPublisher
      .publishEvent(new GroupReplacedEvent(this, updatedGroup, existingGroup, String.format(
          "Replaced group %s with new group %s", existingGroup.getName(), updatedGroup.getName())));

    return converter.toScim(updatedGroup);
  }

  private boolean isGroupNameAvailable(String displayName, String id) {

    return !groupRepository.findByNameWithDifferentId(displayName, id).isPresent();
  }

  @Override
  public ScimListResponse<ScimGroup> list(ScimPageRequest params) {

    if (params.getCount() == 0) {
      int groupCount = groupRepository.countAllGroups();
      return new ScimListResponse<>(Collections.emptyList(), groupCount, 0, 1);
    }

    OffsetPageable op = new OffsetPageable(params.getStartIndex(), params.getCount());

    Page<IamGroup> results = groupRepository.findAll(op);

    List<ScimGroup> resources = new ArrayList<>();

    results.getContent().forEach(g -> resources.add(converter.toScim(g)));

    return new ScimListResponse<>(resources, results.getTotalElements(), resources.size(),
        op.getOffset() + 1);
  }

  @Override
  public void update(String id, List<ScimPatchOperation<List<ScimMemberRef>>> operations) {

    IamGroup iamGroup = groupRepository.findByUuid(id)
      .orElseThrow(() -> new ScimResourceNotFoundException("No group mapped to id '" + id + "'"));

    operations.forEach(op -> executePatchOperation(iamGroup, op));
  }

  private void executePatchOperation(IamGroup group, ScimPatchOperation<List<ScimMemberRef>> op) {

    checkUnsupportedPath(op);

    List<AccountUpdater> updaters = groupUpdaterFactory.getUpdatersForPatchOperation(group, op);
    List<AccountUpdater> updatesToPublish = new ArrayList<>();

    boolean hasChanged = false;

    for (AccountUpdater u : updaters) {
      if (u.update()) {
        IamAccount a = u.getAccount();
        a.touch();
        accountRepository.save(a);
        hasChanged = true;
        updatesToPublish.add(u);
      }
    }

    if (hasChanged) {

      group.touch();
      groupRepository.save(group);
      for (AccountUpdater u : updatesToPublish) {
        u.publishUpdateEvent(this, eventPublisher);
      }
    }
  }

  private void checkUnsupportedPath(ScimPatchOperation<List<ScimMemberRef>> op) {

    if (op.getPath() == null || op.getPath().isEmpty()) {
      throw new ScimPatchOperationNotSupported("empty path value is not currently supported");
    }
    if (op.getPath().equals("members")) {
      return;
    }
    throw new ScimPatchOperationNotSupported(
        "path value " + op.getPath() + " is not currently supported");
  }

  private void displayNameSanityChecks(String displayName) {
    if (Strings.isNullOrEmpty(displayName)) {
      throw new IllegalArgumentException("Group displayName cannot be empty");
    }

    if (displayName.contains("/")) {
      throw new IllegalArgumentException("Group displayName cannot contain a slash character");
    }

    if (displayName.length() > GROUP_NAME_MAX_LENGTH) {
      throw new IllegalArgumentException(
          format("Group name length cannot be higher than %d characters", GROUP_NAME_MAX_LENGTH));
    }
  }

  private void fullNameSanityChecks(String displayName) {
    if (displayName.length() > GROUP_FULLNAME_MAX_LENGTH) {
      throw new IllegalArgumentException(
          format("Group displayName length cannot be higher than %d characters",
              GROUP_FULLNAME_MAX_LENGTH));
    }

    if (groupRepository.findByName(displayName).isPresent()) {
      throw new ScimResourceExistsException(format("Duplicated group '%s'", displayName));
    }
  }

}
