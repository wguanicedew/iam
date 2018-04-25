package it.infn.mw.iam.api.account.search;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.google.common.collect.Lists;
import it.infn.mw.iam.api.account.search.model.IamGroupDTO;
import it.infn.mw.iam.api.common.ListResponseDTO;
import it.infn.mw.iam.api.common.OffsetPageable;
import it.infn.mw.iam.api.common.PagedResourceService;
import it.infn.mw.iam.persistence.model.IamGroup;

@RestController
@Transactional
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping(GroupSearchController.GROUP_SEARCH_ENDPOINT)
public class GroupSearchController {

  public static final String GROUP_SEARCH_ENDPOINT = "/iam/group/search";
  public static final int ITEMS_PER_PAGE = 10;

  @Autowired
  private PagedResourceService<IamGroup> groupService;

  @RequestMapping(method = RequestMethod.GET)
  public ListResponseDTO<IamGroupDTO> getGroups(
      @RequestParam(required = false, defaultValue = "") String filter,
      @RequestParam(required = false, defaultValue = "1") int startIndex,
      @RequestParam(required = false, defaultValue = "10") int count) {


    ListResponseDTO.Builder<IamGroupDTO> response = new ListResponseDTO.Builder<>();

    if (count == 0) {

      /* returns total amount of groups - no resources */
      long totalResults = 0;

      if (filter.isEmpty()) {

        totalResults = groupService.count();

      } else {

        totalResults = groupService.count(filter);
      }

      response.totalResults(totalResults);

    } else {

      OffsetPageable op = new OffsetPageable(startIndex - 1, count);

      Page<IamGroup> groups;

      if (filter.isEmpty()) {

        groups = groupService.getPage(op);

      } else {

        groups = groupService.getPage(op, filter);
      }

      List<IamGroupDTO> resources = Lists.newArrayList();
      groups.getContent()
          .forEach(group -> resources.add(IamGroupDTO.builder().fromIamGroup(group).build()));
      response.resources(resources);
      response.itemsPerPage(groups.getNumberOfElements());
      response.startIndex(startIndex);
      response.totalResults(groups.getTotalElements());

    }

    return response.build();
  }

}
