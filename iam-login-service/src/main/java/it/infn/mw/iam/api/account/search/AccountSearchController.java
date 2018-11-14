/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2018
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import it.infn.mw.iam.api.scim.converter.UserConverter;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.persistence.model.IamAccount;

@RestController
@Transactional
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping(AccountSearchController.ACCOUNT_SEARCH_ENDPOINT)
public class AccountSearchController extends AbstractSearchController<ScimUser> {

  public static final String ACCOUNT_SEARCH_ENDPOINT = "/iam/account/search";
  public static final int ITEMS_PER_PAGE = 10;

  @Autowired
  private PagedResourceService<IamAccount> accountService;

  @Autowired
  private UserConverter scimUserConverter;

  @Override
  @RequestMapping(method = RequestMethod.GET)
  public ListResponseDTO<ScimUser> list(
      @RequestParam(required = false, defaultValue = "1") int startIndex,
      @RequestParam(required = false, defaultValue = "" + DEFAULT_ITEMS_PER_PAGE) int count,
      @RequestParam(required = false, defaultValue = "") String filter) {

    ListResponseDTO.Builder<ScimUser> response = ListResponseDTO.builder();

    if (count == 0) {

      long totalResults = 0;

      if (filter.isEmpty()) {

        totalResults = accountService.count();

      } else {

        totalResults = accountService.count(filter);
      }

      response.totalResults(totalResults);

    } else {

      OffsetPageable op = getOffsetPageable(startIndex, count);
      Page<IamAccount> p;

      if (filter.isEmpty()) {

        p = accountService.getPage(op);

      } else {

        p = accountService.getPage(op, filter);

      }

      List<ScimUser> resources = Lists.newArrayList();
      p.getContent().forEach(a -> resources.add(scimUserConverter.dtoFromEntity(a)));

      response.resources(resources);
      response.fromPage(p, op);
    }
    return response.build();
  }

}
