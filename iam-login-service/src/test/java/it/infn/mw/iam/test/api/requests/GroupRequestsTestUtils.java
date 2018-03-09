package it.infn.mw.iam.test.api.requests;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

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

  @Autowired
  protected IamGroupRequestRepository groupRequestRepository;

  @Autowired
  protected IamAccountRepository accountRepository;

  @Autowired
  protected IamGroupRepository groupRepository;

  @Autowired
  protected GroupRequestConverter converter;

  protected GroupRequestDto buildGroupRequest(String username, String groupName) {
    GroupRequestDto request = new GroupRequestDto();
    request.setUsername(username);
    request.setGroupName(groupName);
    request.setNotes("Test group request membership");

    return request;
  }

  protected GroupRequestDto savePendingGroupRequest(String username, String groupName) {
    return saveGroupRequest(username, groupName, IamGroupRequestStatus.PENDING);
  }

  protected GroupRequestDto saveApprovedGroupRequest(String username, String groupName) {
    return saveGroupRequest(username, groupName, IamGroupRequestStatus.APPROVED);
  }

  protected GroupRequestDto saveRejectedGroupRequest(String username, String groupName) {
    return saveGroupRequest(username, groupName, IamGroupRequestStatus.REJECTED);
  }

  private GroupRequestDto saveGroupRequest(String username, String groupName,
      IamGroupRequestStatus status) {

    IamGroupRequest iamGroupRequest = new IamGroupRequest();
    iamGroupRequest.setUuid(UUID.randomUUID().toString());
    iamGroupRequest.setAccount(accountRepository.findByUsername(username).get());
    iamGroupRequest.setGroup(groupRepository.findByName(groupName).get());
    iamGroupRequest.setStatus(status);
    iamGroupRequest.setCreationTime(new Date());
    IamGroupRequest result = groupRequestRepository.save(iamGroupRequest);

    return converter.fromEntity(result);
  }
}
