/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2021
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
package it.infn.mw.iam.test.api.account.lifecycle;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.Date;
import java.util.function.Supplier;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.infn.mw.iam.api.account.lifecycle.AccountLifecycleDTO;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.test.api.TestSupport;
import it.infn.mw.iam.test.util.WithAnonymousUser;
import it.infn.mw.iam.test.util.WithMockOAuthUser;
import it.infn.mw.iam.test.util.annotation.IamMockMvcIntegrationTest;
import it.infn.mw.iam.test.util.oauth.MockOAuth2Filter;

@RunWith(SpringJUnit4ClassRunner.class)
@IamMockMvcIntegrationTest
public class AccountLifecycleApiTests extends TestSupport {

  public static final String END_TIME_RESOURCE = "/iam/account/{id}/endTime";

  private static final String EXPECTED_ACCOUNT_NOT_FOUND = "Expected account not found";

  @Autowired
  private IamAccountRepository repo;

  @Autowired
  private MockOAuth2Filter mockOAuth2Filter;

  @Autowired
  private ObjectMapper mapper;

  @Autowired
  private MockMvc mvc;


  @Before
  public void setup() {
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
  public void managingEndTimeRequiresAuthenticatedUser() throws Exception {
    AccountLifecycleDTO dto = new AccountLifecycleDTO();
    mvc
      .perform(put(END_TIME_RESOURCE, TEST_100_USER_UUID).content(mapper.writeValueAsString(dto))
        .contentType(APPLICATION_JSON))
      .andExpect(UNAUTHORIZED);
  }

  @Test
  @WithMockUser(username = "test")
  public void managingEndTimeFailsForNormalUser() throws Exception {
    AccountLifecycleDTO dto = new AccountLifecycleDTO();
    mvc
      .perform(put(END_TIME_RESOURCE, TEST_100_USER_UUID).content(mapper.writeValueAsString(dto))
        .contentType(APPLICATION_JSON))
      .andExpect(FORBIDDEN);
  }

  @Test
  @WithMockUser(username = "admin", roles = "ADMIN")
  public void managingEndTimeRequiresAdminUser() throws Exception {
    Date newEndTime = new Date();
    AccountLifecycleDTO dto = new AccountLifecycleDTO();
    dto.setEndTime(newEndTime);

    mvc
      .perform(put(END_TIME_RESOURCE, TEST_100_USER_UUID).content(mapper.writeValueAsString(dto))
        .contentType(APPLICATION_JSON))
      .andExpect(OK);

    IamAccount account =
        repo.findByUuid(TEST_100_USER_UUID).orElseThrow(assertionError(EXPECTED_ACCOUNT_NOT_FOUND));
    
    assertThat(account.getEndTime(), is(newEndTime));
  }

  @Test
  @WithMockOAuthUser(user = "admin", authorities = "ROLE_ADMIN", scopes = "iam:admin.write")
  public void setEndTimeWorksForAdminUserWithScope() throws Exception {
    Date newEndTime = new Date();
    AccountLifecycleDTO dto = new AccountLifecycleDTO();
    dto.setEndTime(newEndTime);

    mvc
      .perform(put(END_TIME_RESOURCE, TEST_100_USER_UUID).content(mapper.writeValueAsString(dto))
        .contentType(APPLICATION_JSON))
      .andExpect(OK);
  }

  @Test
  @WithMockOAuthUser(user = "admin", authorities = "ROLE_ADMIN")
  public void setEndTimeDoesNotWork() throws Exception {
    Date newEndTime = new Date();
    AccountLifecycleDTO dto = new AccountLifecycleDTO();
    dto.setEndTime(newEndTime);

    mvc
      .perform(put(END_TIME_RESOURCE, TEST_100_USER_UUID).content(mapper.writeValueAsString(dto))
        .contentType(APPLICATION_JSON))
      .andExpect(FORBIDDEN);
  }




}
