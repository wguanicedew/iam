package it.infn.mw.iam.api.requests.service;

import it.infn.mw.iam.api.common.ListResponseDTO;
import it.infn.mw.iam.api.common.OffsetPageable;
import it.infn.mw.iam.api.requests.model.GroupRequestDto;

public interface GroupRequestsService {

  GroupRequestDto createGroupRequest(GroupRequestDto groupRequest);

  void deleteGroupRequest(String requestId);

  GroupRequestDto approveGroupRequest(String requestId);

  GroupRequestDto rejectGroupRequest(String requestId, String motivation);

  GroupRequestDto getGroupRequestDetails(String requestId);

  ListResponseDTO<GroupRequestDto> listGroupRequests(String username, String groupName,
      String status, OffsetPageable pageRequest);

}
