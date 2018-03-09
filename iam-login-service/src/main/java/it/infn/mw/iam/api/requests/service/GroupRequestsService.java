package it.infn.mw.iam.api.requests.service;

import it.infn.mw.iam.api.requests.model.GroupRequestDto;

public interface GroupRequestsService {

  GroupRequestDto createGroupRequest(GroupRequestDto groupRequest);

  void deleteGroupRequest(String uuid);

  void approveGroupRequest(String uuid);

  void rejectGroupRequest(String uuid);

  GroupRequestDto getGroupRequestDetails(String uuid);

}
