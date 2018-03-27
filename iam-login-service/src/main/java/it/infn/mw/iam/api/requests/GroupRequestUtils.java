package it.infn.mw.iam.api.requests;

import static it.infn.mw.iam.core.IamGroupRequestStatus.APPROVED;
import static it.infn.mw.iam.core.IamGroupRequestStatus.PENDING;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import it.infn.mw.iam.api.account.AccountUtils;
import it.infn.mw.iam.api.requests.exception.GroupRequestValidationError;
import it.infn.mw.iam.api.requests.model.GroupRequestDto;
import it.infn.mw.iam.core.IamGroupRequestStatus;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamAuthority;
import it.infn.mw.iam.persistence.model.IamGroup;
import it.infn.mw.iam.persistence.model.IamGroupRequest;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.persistence.repository.IamGroupRequestRepository;

@Component
public class GroupRequestUtils {

  @Autowired
  private IamGroupRequestRepository groupRequestRepository;

  @Autowired
  private IamAccountRepository accoutRepository;

  @Autowired
  private AccountUtils accountUtils;

  private static final IamAuthority ROLE_ADMIN = new IamAuthority("ROLE_ADMIN");

  public Optional<IamGroupRequest> getGroupRequestUuid(String uuid) {
    return groupRequestRepository.findByUuid(uuid);
  }

  public IamGroupRequest getGroupRequest(String requestId) {
    return groupRequestRepository.findByUuid(requestId)
      .orElseThrow(() -> new GroupRequestValidationError(
          String.format("Group request with UUID [%s] does not exist", requestId)));
  }

  public void checkRequestAlreadyExist(GroupRequestDto request) {
    Optional<IamGroupRequest> result = groupRequestRepository
      .findByUsernameAndGroup(request.getUsername(), request.getGroupName());
    if (result.isPresent()) {
      IamGroupRequestStatus status = result.get().getStatus();
      if (PENDING.equals(status) || APPROVED.equals(status)) {
        throw new GroupRequestValidationError(
            String.format("Group membership request already exist for [%s, %s]",
                request.getUsername(), request.getGroupName()));
      }
    }
  }

  public void validateRejectMotivation(String motivation) {
    String value = motivation;
    if (motivation != null) {
      value = motivation.trim();
    }

    if (Strings.isNullOrEmpty(value)) {
      throw new GroupRequestValidationError("Reject motivation cannot be empty");
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
        throw new GroupRequestValidationError(
            String.format("User [%s] is already member of the group [%s]", request.getUsername(),
                request.getGroupName()));
      }
    }
  }

  public boolean isPrivilegedUser() {
    Optional<IamAccount> userAccount = accountUtils.getAuthenticatedUserAccount();
    return userAccount.isPresent() && userAccount.get().getAuthorities().contains(ROLE_ADMIN);
  }
}
