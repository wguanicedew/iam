package it.infn.mw.iam.test.api.requests;

import static it.infn.mw.iam.core.IamGroupRequestStatus.APPROVED;
import static it.infn.mw.iam.core.IamGroupRequestStatus.PENDING;
import static it.infn.mw.iam.core.IamGroupRequestStatus.REJECTED;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.infn.mw.iam.api.requests.GroupRequestConverter;
import it.infn.mw.iam.api.requests.model.GroupRequestDto;
import it.infn.mw.iam.core.IamGroupRequestStatus;
import it.infn.mw.iam.persistence.model.IamGroupRequest;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.persistence.repository.IamGroupRepository;
import it.infn.mw.iam.persistence.repository.IamGroupRequestRepository;

public class GroupRequestsTestUtils {

  protected final static String TEST_USERNAME = "test_100";
  protected final static String TEST_GROUPNAME = "Test-001";
  protected final static String TEST_NOTES = "Test group request membership";
  protected final static String TEST_REJECT_MOTIVATION = "You are not welcome!";

  @Autowired
  protected IamGroupRequestRepository groupRequestRepository;

  @Autowired
  protected IamAccountRepository accountRepository;

  @Autowired
  protected IamGroupRepository groupRepository;

  @Autowired
  protected GroupRequestConverter converter;

  @Autowired
  protected ObjectMapper mapper;

  protected GroupRequestDto buildGroupRequest(String username, String groupName) {
    GroupRequestDto request = new GroupRequestDto();
    request.setUsername(username);
    request.setGroupName(groupName);
    request.setNotes(TEST_NOTES);

    return request;
  }

  protected GroupRequestDto savePendingGroupRequest(String username, String groupName) {
    return saveGroupRequest(username, groupName, PENDING);
  }

  protected GroupRequestDto saveApprovedGroupRequest(String username, String groupName) {
    return saveGroupRequest(username, groupName, APPROVED);
  }

  protected GroupRequestDto saveRejectedGroupRequest(String username, String groupName) {
    return saveGroupRequest(username, groupName, REJECTED);
  }

  private GroupRequestDto saveGroupRequest(String username, String groupName,
      IamGroupRequestStatus status) {

    IamGroupRequest iamGroupRequest = new IamGroupRequest();
    iamGroupRequest.setUuid(UUID.randomUUID().toString());
    iamGroupRequest.setAccount(accountRepository.findByUsername(username).get());
    iamGroupRequest.setGroup(groupRepository.findByName(groupName).get());
    iamGroupRequest.setNotes(TEST_NOTES);
    iamGroupRequest.setStatus(status);
    iamGroupRequest.setCreationTime(new Date());
    if (REJECTED.equals(status)) {
      iamGroupRequest.setMotivation(TEST_REJECT_MOTIVATION);
    }

    IamGroupRequest result = groupRequestRepository.save(iamGroupRequest);

    return converter.fromEntity(result);
  }
}
