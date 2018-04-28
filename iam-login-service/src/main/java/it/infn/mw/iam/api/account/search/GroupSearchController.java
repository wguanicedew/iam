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
import it.infn.mw.iam.api.common.ListResponseDTO;
import it.infn.mw.iam.api.common.OffsetPageable;
import it.infn.mw.iam.api.common.PagedResourceService;
import it.infn.mw.iam.api.scim.converter.GroupConverter;
import it.infn.mw.iam.api.scim.model.ScimGroup;
import it.infn.mw.iam.persistence.model.IamGroup;

@RestController
@Transactional
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping(GroupSearchController.GROUP_SEARCH_ENDPOINT)
public class GroupSearchController extends AbstractSearchController<ScimGroup> {

  public static final String GROUP_SEARCH_ENDPOINT = "/iam/group/search";
  public static final int ITEMS_PER_PAGE = 10;

  @Autowired
  private PagedResourceService<IamGroup> groupService;

  @Autowired
  private GroupConverter scimGroupConverter;

  @Override
  @RequestMapping(method = RequestMethod.GET)
  public ListResponseDTO<ScimGroup> list(
      @RequestParam(required = false, defaultValue = "1") int startIndex,
      @RequestParam(required = false, defaultValue = "" + DEFAULT_ITEMS_PER_PAGE) int count,
      @RequestParam(required = false, defaultValue = "") String filter) {

    ListResponseDTO.Builder<ScimGroup> response = ListResponseDTO.builder();

    if (count == 0) {

      long totalResults = 0;

      if (filter.isEmpty()) {

        totalResults = groupService.count();

      } else {

        totalResults = groupService.count(filter);
      }

      response.totalResults(totalResults);

    } else {

      OffsetPageable op = getOffsetPageable(startIndex, count);
      Page<IamGroup> p;

      if (filter.isEmpty()) {

        p = groupService.getPage(op);

      } else {

        p = groupService.getPage(op, filter);

      }

      List<ScimGroup> resources = Lists.newArrayList();
      p.getContent().forEach(g -> resources.add(scimGroupConverter.dtoFromEntity(g)));

      response.resources(resources);
      response.fromPage(p, op);
    }
    return response.build();
  }

}
