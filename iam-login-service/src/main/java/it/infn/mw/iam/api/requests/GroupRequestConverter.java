package it.infn.mw.iam.api.requests;

import org.springframework.stereotype.Service;

import it.infn.mw.iam.api.requests.model.GroupRequestDto;
import it.infn.mw.iam.persistence.model.IamGroupRequest;

@Service
public class GroupRequestConverter {

  public GroupRequestDto fromEntity(IamGroupRequest iamGroupRequest) {
    GroupRequestDto groupRequest = new GroupRequestDto();
    groupRequest.setUuid(iamGroupRequest.getUuid());
    groupRequest.setUsername(iamGroupRequest.getAccount().getUsername());
    groupRequest.setGroupName(iamGroupRequest.getGroup().getName());
    groupRequest.setStatus(iamGroupRequest.getStatus().name());
    groupRequest.setNotes(iamGroupRequest.getNotes());
    groupRequest.setMotivation(iamGroupRequest.getMotivation());
    groupRequest.setCreationTime(iamGroupRequest.getCreationTime());
    groupRequest.setLastUpdateTime(iamGroupRequest.getLastUpdateTime());

    return groupRequest;
  }
}
