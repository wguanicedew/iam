package it.infn.mw.iam.api.requests.service;

import static it.infn.mw.iam.core.IamGroupRequestStatus.APPROVED;
import static it.infn.mw.iam.core.IamGroupRequestStatus.PENDING;
import static it.infn.mw.iam.core.IamGroupRequestStatus.REJECTED;
import static it.infn.mw.iam.core.IamGroupRequestStatus.valueOf;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;

import it.infn.mw.iam.api.account.AccountUtils;
import it.infn.mw.iam.api.common.ListResponseDTO;
import it.infn.mw.iam.api.common.OffsetPageable;
import it.infn.mw.iam.api.requests.GroupRequestConverter;
import it.infn.mw.iam.api.requests.GroupRequestUtils;
import it.infn.mw.iam.api.requests.exception.GroupRequestStatusException;
import it.infn.mw.iam.api.requests.exception.GroupRequestValidationException;
import it.infn.mw.iam.api.requests.model.GroupRequestDto;
import it.infn.mw.iam.audit.events.group.request.GroupRequestApprovedEvent;
import it.infn.mw.iam.audit.events.group.request.GroupRequestCreatedEvent;
import it.infn.mw.iam.audit.events.group.request.GroupRequestDeletedEvent;
import it.infn.mw.iam.audit.events.group.request.GroupRequestRejectedEvent;
import it.infn.mw.iam.core.IamGroupRequestStatus;
import it.infn.mw.iam.notification.NotificationFactory;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamGroup;
import it.infn.mw.iam.persistence.model.IamGroupRequest;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.persistence.repository.IamGroupRequestRepository;

@Service
public class DefaultGroupRequestsService implements GroupRequestsService {

  @Autowired
  public IamGroupRequestRepository groupRequestRepository;

  @Autowired
  private IamAccountRepository accoutRepository;

  @Autowired
  private GroupRequestConverter converter;

  @Autowired
  private AccountUtils accountUtils;

  @Autowired
  private GroupRequestUtils groupRequestUtils;

  @Autowired
  private NotificationFactory notificationFactory;

  @Autowired
  private ApplicationEventPublisher eventPublisher;

  private static final Table<IamGroupRequestStatus, IamGroupRequestStatus, Boolean> allowedStateTransitions =
      new ImmutableTable.Builder<IamGroupRequestStatus, IamGroupRequestStatus, Boolean>()
        .put(PENDING, APPROVED, true)
        .put(PENDING, REJECTED, true)
        .build();

  @Override
  public GroupRequestDto createGroupRequest(GroupRequestDto groupRequest) {

    groupRequestUtils.validateMandatoryFields(groupRequest);

    IamAccount account = groupRequestUtils.validateAccount(groupRequest.getUsername());
    IamGroup group = groupRequestUtils.validateGroup(groupRequest.getGroupName());

    groupRequestUtils.checkRequestAlreadyExist(groupRequest);
    groupRequestUtils.checkUserMembership(groupRequest);

    IamGroupRequest iamGroupRequest = new IamGroupRequest();
    iamGroupRequest.setUuid(UUID.randomUUID().toString());
    iamGroupRequest.setAccount(account);
    iamGroupRequest.setGroup(group);
    iamGroupRequest.setNotes(groupRequest.getNotes());
    iamGroupRequest.setStatus(PENDING);
    iamGroupRequest.setCreationTime(new Date());

    IamGroupRequest result = groupRequestRepository.save(iamGroupRequest);
    notificationFactory.createAdminHandleGroupRequestMessage(iamGroupRequest);
    eventPublisher.publishEvent(new GroupRequestCreatedEvent(this, result));

    return converter.fromEntity(result);
  }

  @Override
  public void deleteGroupRequest(String uuid) {
    IamGroupRequest request = groupRequestUtils.validateGroupRequestUuid(uuid);

    groupRequestRepository.delete(request.getId());
    eventPublisher.publishEvent(new GroupRequestDeletedEvent(this, request));
  }

  @Override
  public GroupRequestDto approveGroupRequest(String uuid) {
    IamGroupRequest request = groupRequestUtils.validateGroupRequestUuid(uuid);

    IamAccount account = request.getAccount();
    IamGroup group = request.getGroup();
    account.getGroups().add(group);

    accoutRepository.save(account);
    request = updateGroupRequestStatus(request, APPROVED);
    notificationFactory.createGroupMembershipApprovedMessage(request);
    eventPublisher.publishEvent(new GroupRequestApprovedEvent(this, request));

    return converter.fromEntity(request);
  }

  @Override
  public GroupRequestDto rejectGroupRequest(String uuid, String motivation) {
    IamGroupRequest request = groupRequestUtils.validateGroupRequestUuid(uuid);
    groupRequestUtils.validateRejectMotivation(motivation);

    request.setMotivation(motivation);
    request = updateGroupRequestStatus(request, REJECTED);
    notificationFactory.createGroupMembershipRejectedMessage(request);
    eventPublisher.publishEvent(new GroupRequestRejectedEvent(this, request));

    return converter.fromEntity(request);
  }

  @Override
  public GroupRequestDto getGroupRequestDetails(String uuid) {
    Optional<IamGroupRequest> request = groupRequestUtils.getGroupRequestUuid(uuid);
    if (!request.isPresent()) {
      throw new GroupRequestValidationException(
          String.format("Group request with id [%s] does not exist", uuid));
    }
    return converter.fromEntity(request.get());
  }

  @Override
  public ListResponseDTO<GroupRequestDto> listGroupRequest(String username, String groupName,
      String status, OffsetPageable pageRequest) {
    Optional<String> usernameFilter = Optional.ofNullable(username);
    Optional<String> groupNameFilter = Optional.ofNullable(groupName);
    Optional<String> statusFilter = Optional.ofNullable(status);

    if (!groupRequestUtils.isPrivilegedUser()) {
      if (usernameFilter.isPresent()) {
        groupRequestUtils.validateUserAuth(username);
      } else {
        Optional<IamAccount> userAccount = accountUtils.getAuthenticatedUserAccount();
        if (userAccount.isPresent()) {
          usernameFilter = Optional.of(userAccount.get().getUsername());
        }
      }
    }

    List<GroupRequestDto> elementList = new ArrayList<>();

    Page<IamGroupRequest> result =
        filterRequest(usernameFilter, groupNameFilter, statusFilter, pageRequest);
    result.getContent().forEach(request -> elementList.add(converter.fromEntity(request)));

    ListResponseDTO.Builder<GroupRequestDto> builder = ListResponseDTO.builder();
    return builder.resources(elementList).fromPage(result, pageRequest).build();
  }

  private IamGroupRequest updateGroupRequestStatus(IamGroupRequest request,
      IamGroupRequestStatus status) {

    if (!allowedStateTransitions.contains(request.getStatus(), status)) {
      throw new GroupRequestStatusException(
          String.format("Group request wrong transition: %s -> %s", request.getStatus(), status));
    }
    request.setStatus(status);
    request.setLastUpdateTime(new Date());
    return groupRequestRepository.save(request);
  }

  private Page<IamGroupRequest> filterRequest(Optional<String> usernameFilter,
      Optional<String> groupNameFilter, Optional<String> statusFilter, OffsetPageable pageRequest) {

    if (usernameFilter.isPresent() && statusFilter.isPresent()) {
      return groupRequestRepository.findByUsernameAndStatus(usernameFilter.get(),
          valueOf(statusFilter.get()), pageRequest);
    }

    if (usernameFilter.isPresent()) {
      return groupRequestRepository.findByUsername(usernameFilter.get(), pageRequest);
    }

    if (groupNameFilter.isPresent() && statusFilter.isPresent()) {
      return groupRequestRepository.findByGroupAndStatus(groupNameFilter.get(),
          valueOf(statusFilter.get()), pageRequest);
    }

    if (groupNameFilter.isPresent()) {
      return groupRequestRepository.findByGroup(groupNameFilter.get(), pageRequest);
    }

    if (statusFilter.isPresent()) {
      return groupRequestRepository.findByStatus(valueOf(statusFilter.get()), pageRequest);
    }

    return groupRequestRepository.findAll(pageRequest);
  }

}
