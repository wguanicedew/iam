package it.infn.mw.iam.api.requests;

import static it.infn.mw.iam.core.IamGroupRequestStatus.APPROVED;
import static it.infn.mw.iam.core.IamGroupRequestStatus.PENDING;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import it.infn.mw.iam.api.account.AccountUtils;
import it.infn.mw.iam.api.requests.exception.GroupRequestValidationException;
import it.infn.mw.iam.api.requests.exception.UserMismatchException;
import it.infn.mw.iam.api.requests.model.GroupRequestDto;
import it.infn.mw.iam.core.IamGroupRequestStatus;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamAuthority;
import it.infn.mw.iam.persistence.model.IamGroup;
import it.infn.mw.iam.persistence.model.IamGroupRequest;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.persistence.repository.IamGroupRepository;
import it.infn.mw.iam.persistence.repository.IamGroupRequestRepository;

@Component
public class GroupRequestUtils {

  @Autowired
  private IamGroupRequestRepository groupRequestRepository;

  @Autowired
  private IamAccountRepository accoutRepository;

  @Autowired
  private IamGroupRepository groupRepository;

  @Autowired
  private AccountUtils accountUtils;

  private static final IamAuthority ROLE_ADMIN = new IamAuthority("ROLE_ADMIN");

  public Optional<IamGroupRequest> getGroupRequestUuid(String uuid) {
    return groupRequestRepository.findByUuid(uuid);
  }

  public IamGroupRequest validateGroupRequestUuid(String uuid) {
    if (Strings.isNullOrEmpty(uuid)) {
      throw new GroupRequestValidationException("uuid cannot be empty");
    }

    return groupRequestRepository.findByUuid(uuid).orElseThrow(
        () -> new GroupRequestValidationException(buildExceptionMessage("Group request", uuid)));
  }

  public void validateMandatoryFields(GroupRequestDto request) {
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

  public void validateRejectMotivation(String motivation) {
    String value = motivation;
    if (motivation != null) {
      value = motivation.trim();
    }

    if (Strings.isNullOrEmpty(value)) {
      throw new GroupRequestValidationException("Reject motivation cannot be empty");
    }
  }

  public IamAccount validateAccount(String username) {
    return accoutRepository.findByUsername(username).orElseThrow(
        () -> new GroupRequestValidationException(buildExceptionMessage("Account", username)));
  }

  public IamGroup validateGroup(String groupName) {
    return groupRepository.findByName(groupName).orElseThrow(
        () -> new GroupRequestValidationException(buildExceptionMessage("Group", groupName)));
  }

  public void checkRequestAlreadyExist(GroupRequestDto request) {
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

  public void checkUserMembership(GroupRequestDto request) {
    Optional<IamAccount> userAccount = accoutRepository.findByUsername(request.getUsername());
    if (userAccount.isPresent()) {
      Optional<IamGroup> group = userAccount.get()
        .getGroups()
        .stream()
        .filter(g -> g.getName().equals(request.getGroupName()))
        .findAny();

      if (group.isPresent()) {
        throw new GroupRequestValidationException(
            String.format("User [%s] is already member of the group [%s]", request.getUsername(),
                request.getGroupName()));
      }
    }
  }

  private String buildExceptionMessage(String subject, Object value) {
    return String.format("%s with id [%s] does not exist", subject, value);
  }

  public void validateUserAuth(String requestUsername) {
    Optional<IamAccount> account = accountUtils.getAuthenticatedUserAccount();
    if (account.isPresent()) {
      String username = account.get().getUsername();
      if (!username.equals(requestUsername)) {
        throw new UserMismatchException("Cannot handle requests of another user");
      }
    }
  }

  public boolean isPrivilegedUser() {
    Optional<IamAccount> userAccount = accountUtils.getAuthenticatedUserAccount();
    return userAccount.isPresent() && userAccount.get().getAuthorities().contains(ROLE_ADMIN);
  }

}
