package it.infn.mw.iam.api.requests.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import it.infn.mw.iam.api.requests.model.GroupRequestDto;

public interface GroupRequestsService {

  GroupRequestDto createGroupRequest(GroupRequestDto groupRequest);

  void deleteGroupRequest(String uuid);

  void approveGroupRequest(String uuid);

  void rejectGroupRequest(String uuid, String motivation);

  GroupRequestDto getGroupRequestDetails(String uuid);

  List<GroupRequestDto> listGroupRequest(String username, String groupName, String status,
      Pageable pageRequest);

}
