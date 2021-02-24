/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2019
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
package it.infn.mw.iam.test.api.group;

import static it.infn.mw.iam.api.group.find.FindGroupController.FIND_BY_LABEL_RESOURCE;
import static it.infn.mw.iam.api.group.find.FindGroupController.FIND_BY_NAME_RESOURCE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.emptyIterable;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.function.Supplier;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.persistence.model.IamGroup;
import it.infn.mw.iam.persistence.repository.IamGroupRepository;
import it.infn.mw.iam.test.api.TestSupport;
import it.infn.mw.iam.test.core.CoreControllerTestSupport;
import it.infn.mw.iam.test.util.WithAnonymousUser;
import it.infn.mw.iam.test.util.oauth.MockOAuth2Filter;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class, CoreControllerTestSupport.class})
@WebAppConfiguration
@Transactional
@WithMockUser(username = "admin", roles = "ADMIN")
public class FindGroupTests extends TestSupport {

  @Autowired
  private WebApplicationContext context;

  private MockMvc mvc;

  @Autowired
  private MockOAuth2Filter mockOAuth2Filter;

  @Autowired
  private IamGroupRepository repo;

  @Before
  public void setup() {

    mvc =
        MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).alwaysDo(log()).build();
    mockOAuth2Filter.cleanupSecurityContext();
  }

  @After
  public void cleanupOAuthUser() {
    mockOAuth2Filter.cleanupSecurityContext();
  }

  private Supplier<AssertionError> assertionError(String message) {
    return () -> new AssertionError(message);
  }

  @Test
  @WithAnonymousUser
  public void findingRequiresAuthenticatedUser() throws Exception {

    mvc
      .perform(get(FIND_BY_LABEL_RESOURCE).param("name", "test")
        .param("value", "test"))
      .andExpect(UNAUTHORIZED);

    mvc.perform(get(FIND_BY_NAME_RESOURCE).param("name", "test"))
      .andExpect(UNAUTHORIZED);

  }

  @Test
  @WithMockUser(username = "test", roles = "USER")
  public void findingRequiresAdminUser() throws Exception {

    mvc
      .perform(get(FIND_BY_LABEL_RESOURCE).param("name", "test")
        .param("value", "test"))
      .andExpect(FORBIDDEN);

    mvc.perform(get(FIND_BY_NAME_RESOURCE).param("name", "test"))
      .andExpect(FORBIDDEN);

  }

  @Test
  public void findByNameWorks() throws Exception {


    IamGroup group =
        repo.findByUuid(TEST_001_GROUP_UUID).orElseThrow(assertionError(EXPECTED_GROUP_NOT_FOUND));

    mvc.perform(get(FIND_BY_NAME_RESOURCE).param("name", group.getName()))
      .andExpect(OK)
      .andExpect(jsonPath("$.totalResults", is(1)))
      .andExpect(jsonPath("$.Resources[0].id", is(TEST_001_GROUP_UUID)))
      .andExpect(jsonPath("$.Resources[0].members").doesNotExist());

    mvc.perform(get(FIND_BY_NAME_RESOURCE).param("name", "unknown_group"))
      .andExpect(OK)
      .andExpect(jsonPath("$.totalResults").doesNotExist())
      .andExpect(jsonPath("$.Resources", emptyIterable()));

  }


}
