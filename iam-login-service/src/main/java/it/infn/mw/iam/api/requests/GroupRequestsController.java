package it.infn.mw.iam.api.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.infn.mw.iam.api.common.ListResponseDTO;
import it.infn.mw.iam.api.common.OffsetPageable;
import it.infn.mw.iam.api.common.PagingUtils;
import it.infn.mw.iam.api.requests.model.GroupRequestDto;
import it.infn.mw.iam.api.requests.service.GroupRequestsService;

@RestController
@RequestMapping("/iam/group_requests")
public class GroupRequestsController {

  private static final Integer GROUP_REQUEST_MAX_PAGE_SIZE = 10;

  @Autowired
  private GroupRequestsService groupRequestService;

  @RequestMapping(method = RequestMethod.POST, value = {"", "/"})
  @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
  public GroupRequestDto createGroupRequest(@RequestBody GroupRequestDto groupRequest) {
    return groupRequestService.createGroupRequest(groupRequest);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/")
  @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
  public ListResponseDTO<GroupRequestDto> listGroupRequest(
      @RequestParam(required = false) String username,
      @RequestParam(required = false) String groupName,
      @RequestParam(required = false) String status, @RequestParam(required = false) Integer count,
      @RequestParam(required = false) Integer startIndex) {

    OffsetPageable pageRequest =
        PagingUtils.buildPageRequest(count, startIndex, GROUP_REQUEST_MAX_PAGE_SIZE);
    return groupRequestService.listGroupRequest(username, groupName, status, pageRequest);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/{uuid}")
  @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
  public GroupRequestDto getGroupRequestDetails(@PathVariable("uuid") String uuid) {
    return groupRequestService.getGroupRequestDetails(uuid);
  }

  @RequestMapping(method = RequestMethod.DELETE, value = "/{uuid}")
  @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteGroupRequest(@PathVariable("uuid") String uuid) {
    groupRequestService.deleteGroupRequest(uuid);
  }

  @RequestMapping(method = RequestMethod.POST, value = "/{uuid}/approve")
  @PreAuthorize("hasRole('ADMIN')")
  @ResponseStatus(HttpStatus.OK)
  public GroupRequestDto approveGroupRequest(@PathVariable("uuid") String uuid) {
    return groupRequestService.approveGroupRequest(uuid);
  }

  @RequestMapping(method = RequestMethod.POST, value = "/{uuid}/reject")
  @PreAuthorize("hasRole('ADMIN')")
  @ResponseStatus(HttpStatus.OK)
  public GroupRequestDto rejectGroupRequest(@PathVariable("uuid") String uuid,
      @RequestParam(required = false) String motivation) {
    return groupRequestService.rejectGroupRequest(uuid, motivation);
  }

}
