package it.infn.mw.iam.api.requests.service;

import static it.infn.mw.iam.core.IamGroupRequestStatus.APPROVED;
import static it.infn.mw.iam.core.IamGroupRequestStatus.PENDING;
import static it.infn.mw.iam.core.IamGroupRequestStatus.REJECTED;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;

import it.infn.mw.iam.api.requests.GroupRequestConverter;
import it.infn.mw.iam.api.requests.exception.GroupRequestStatusException;
import it.infn.mw.iam.api.requests.exception.GroupRequestValidationException;
import it.infn.mw.iam.api.requests.exception.UserMismatchException;
import it.infn.mw.iam.api.requests.model.GroupRequestDto;
import it.infn.mw.iam.api.scim.exception.IllegalArgumentException;
import it.infn.mw.iam.core.IamGroupRequestStatus;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamGroup;
import it.infn.mw.iam.persistence.model.IamGroupRequest;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.persistence.repository.IamGroupRepository;
import it.infn.mw.iam.persistence.repository.IamGroupRequestRepository;

@Service
public class DefaultGroupRequestsService implements GroupRequestsService {

  @Autowired
  private IamGroupRequestRepository groupRequestRepository;

  @Autowired
  private IamAccountRepository accoutRepository;

  @Autowired
  private IamGroupRepository groupRepository;

  @Autowired
  private GroupRequestConverter converter;

  private static final Table<IamGroupRequestStatus, IamGroupRequestStatus, Boolean> allowedStateTransitions =
      new ImmutableTable.Builder<IamGroupRequestStatus, IamGroupRequestStatus, Boolean>()
        .put(PENDING, APPROVED, true)
        .put(PENDING, REJECTED, true)
        .build();

  private static final SimpleGrantedAuthority ROLE_ADMIN = new SimpleGrantedAuthority("ROLE_ADMIN");

  @Override
  public GroupRequestDto createGroupRequest(GroupRequestDto groupRequest) {

    checkMandatoryFields(groupRequest);

    IamAccount account = checkAccount(groupRequest.getUsername());
    IamGroup group = checkGroup(groupRequest.getGroupName());

    checkRequestAlreadyExist(groupRequest);

    if (!isPrivilegedUser()) {
      validateUserAuth(account.getUsername());
    }

    IamGroupRequest iamGroupRequest = new IamGroupRequest();
    iamGroupRequest.setUuid(UUID.randomUUID().toString());
    iamGroupRequest.setAccount(account);
    iamGroupRequest.setGroup(group);
    iamGroupRequest.setStatus(PENDING);
    iamGroupRequest.setCreationTime(new Date());

    IamGroupRequest result = groupRequestRepository.save(iamGroupRequest);
    return converter.fromEntity(result);
  }

  @Override
  public void deleteGroupRequest(String uuid) {
    IamGroupRequest request = checkGroupRequestUuid(uuid);

    if (!isPrivilegedUser()) {
      validateUserAuth(request.getAccount().getUsername());
      if (!PENDING.equals(request.getStatus())) {
        throw new GroupRequestStatusException("Cannot delete not pending requests");
      }
    }

    groupRequestRepository.delete(request.getId());
  }

  @Override
  public void approveGroupRequest(String uuid) {
    IamGroupRequest request = checkGroupRequestUuid(uuid);

    IamAccount account = request.getAccount();
    IamGroup group = request.getGroup();
    account.getGroups().add(group);

    accoutRepository.save(account);
    updateGroupRequestStatus(request, APPROVED);
  }

  @Override
  public void rejectGroupRequest(String uuid, String motivation) {
    IamGroupRequest request = checkGroupRequestUuid(uuid);
    checkRejectMotivation(motivation);

    request.setMotivation(motivation);
    updateGroupRequestStatus(request, REJECTED);
  }

  @Override
  public GroupRequestDto getGroupRequestDetails(String uuid) {
    IamGroupRequest request = checkGroupRequestUuid(uuid);
    if (!isPrivilegedUser()) {
      validateUserAuth(request.getAccount().getUsername());
    }
    return converter.fromEntity(request);
  }

  @Override
  public List<GroupRequestDto> listGroupRequest(String username, String groupName, String status,
      Pageable pageRequest) {

    Optional<String> usernameFilter = Optional.ofNullable(username);
    Optional<String> groupNameFilter = Optional.ofNullable(groupName);
    Optional<String> statusFilter = Optional.ofNullable(status);

    Page<IamGroupRequest> result = null;
    List<GroupRequestDto> requestList = new ArrayList<>();

    if (usernameFilter.isPresent()) {
      result = groupRequestRepository.findByUsername(username, pageRequest);
    } else if (groupNameFilter.isPresent()) {
      result = groupRequestRepository.findByGroup(groupName, pageRequest);
    } else if (statusFilter.isPresent()) {
      result =
          groupRequestRepository.findByStatus(IamGroupRequestStatus.valueOf(status), pageRequest);
    } else {
      result = groupRequestRepository.findAll(pageRequest);
    }

    result.getContent().forEach(request -> requestList.add(converter.fromEntity(request)));

    return requestList;
  }


  private void updateGroupRequestStatus(IamGroupRequest request, IamGroupRequestStatus status) {
    if (allowedStateTransitions.contains(request.getStatus(), status)) {
      request.setStatus(status);
      request.setLastUpdateTime(new Date());
      groupRequestRepository.save(request);
    } else {
      throw new GroupRequestStatusException(
          String.format("Group request wrong transition: %s -> %s", request.getStatus(), status));
    }
  }

  private IamGroupRequest checkGroupRequestUuid(String uuid) {
    if (Strings.isNullOrEmpty(uuid)) {
      throw new GroupRequestValidationException("uuid cannot be empty");
    }

    return groupRequestRepository.findByUuid(uuid).orElseThrow(
        () -> new GroupRequestValidationException(buildExceptionMessage("Group request", uuid)));
  }

  private void checkMandatoryFields(GroupRequestDto request) {
    if (Strings.isNullOrEmpty(request.getUsername())) {
      throw new GroupRequestValidationException("Username cannot be empty");
    }

    if (Strings.isNullOrEmpty(request.getGroupName())) {
      throw new GroupRequestValidationException("Group name cannot be empty");
    }

    String notes = request.getNotes();
    if (notes != null) {
      notes = request.getNotes().trim();
    }

    if (Strings.isNullOrEmpty(notes)) {
      throw new GroupRequestValidationException("Notes cannot be empty");
    }
  }

  private void checkRejectMotivation(String motivation) {
    String value = motivation;
    if (motivation != null) {
      value = motivation.trim();
    }

    if (Strings.isNullOrEmpty(value)) {
      throw new GroupRequestValidationException("Reject motivation cannot be empty");
    }
  }

  private IamAccount checkAccount(String username) {
    return accoutRepository.findByUsername(username).orElseThrow(
        () -> new GroupRequestValidationException(buildExceptionMessage("Account", username)));
  }

  private IamGroup checkGroup(String groupName) {
    return groupRepository.findByName(groupName).orElseThrow(
        () -> new GroupRequestValidationException(buildExceptionMessage("Group", groupName)));
  }

  private void checkRequestAlreadyExist(GroupRequestDto request) {
    Optional<IamGroupRequest> result = groupRequestRepository
      .findByUsernameAndGroup(request.getUsername(), request.getGroupName());
    if (result.isPresent()) {
      IamGroupRequestStatus status = result.get().getStatus();
      if (PENDING.equals(status) || APPROVED.equals(status)) {
        throw new GroupRequestValidationException(
            String.format("Group membership request already exist for [%s, %s]",
                request.getUsername(), request.getGroupName()));
      }
    }
  }

  private String buildExceptionMessage(String subject, Object value) {
    return String.format("%s with id [%s] does not exist", subject, value);
  }

  private boolean isPrivilegedUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth instanceof OAuth2Authentication) {
      OAuth2Authentication oauth = (OAuth2Authentication) auth;
      if (oauth.getUserAuthentication() == null) {
        throw new IllegalArgumentException("No user linked to the current OAuth token");
      }
      auth = oauth.getUserAuthentication();
    }

    return auth.getAuthorities().contains(ROLE_ADMIN);
  }

  private void validateUserAuth(String requestUsername) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    String username = auth.getName();
    if (!username.equals(requestUsername)) {
      throw new UserMismatchException("Cannot handle requests of another user");
    }
  }

}
