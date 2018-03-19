package it.infn.mw.iam.api.requests.service;

import it.infn.mw.iam.api.common.ListResponseDTO;
import it.infn.mw.iam.api.common.OffsetPageable;
import it.infn.mw.iam.api.requests.model.GroupRequestDto;

public interface GroupRequestsService {

  GroupRequestDto createGroupRequest(GroupRequestDto groupRequest);

  void deleteGroupRequest(String uuid);

  void approveGroupRequest(String uuid);

  void rejectGroupRequest(String uuid, String motivation);

  GroupRequestDto getGroupRequestDetails(String uuid);

  ListResponseDTO<GroupRequestDto> listGroupRequest(String username, String groupName,
      String status, OffsetPageable pageRequest);

}
