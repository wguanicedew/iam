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
package it.infn.mw.iam.test.client.management;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.testcontainers.shaded.com.google.common.collect.Sets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.infn.mw.iam.api.common.client.AuthorizationGrantType;
import it.infn.mw.iam.api.common.client.RegisteredClientDTO;
import it.infn.mw.iam.test.util.WithAnonymousUser;
import it.infn.mw.iam.test.util.annotation.IamMockMvcIntegrationTest;
import it.infn.mw.iam.test.util.oauth.MockOAuth2Filter;


@RunWith(SpringRunner.class)
@IamMockMvcIntegrationTest
class ClientManagementAPIControllerTests {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private MockOAuth2Filter mockOAuth2Filter;

  @Autowired
  private ObjectMapper mapper;

  public static final String IAM_CLIENTS_API_URL = "/iam/api/clients/";

  public static final ResultMatcher UNAUTHORIZED = status().isUnauthorized();
  public static final ResultMatcher BAD_REQUEST = status().isBadRequest();

  @Before
  public void setup() {
    mockOAuth2Filter.cleanupSecurityContext();
  }

  @After
  public void cleanupOAuthUser() {
    mockOAuth2Filter.cleanupSecurityContext();
  }

  @Test
  @WithAnonymousUser
  public void createClientWithAnonymousUser() throws JsonProcessingException, Exception {

    RegisteredClientDTO client = new RegisteredClientDTO();
    client.setClientName("test-client-creation");
    client.setClientId("test-client-creation");
    client.setGrantTypes(Sets.newHashSet(AuthorizationGrantType.CLIENT_CREDENTIALS));
    client.setScope(Sets.newHashSet("test"));

    mvc
      .perform(post(IAM_CLIENTS_API_URL).contentType(APPLICATION_JSON)
        .content(mapper.writeValueAsString(client)))
      .andExpect(UNAUTHORIZED);
  }

  @Test
  @WithMockUser(username = "admin", roles = "ADMIN")
  public void createClientRaiseParseException() throws JsonProcessingException, Exception {

    final String NOT_A_JSON_STRING = "This is not a JSON string";
    
    RegisteredClientDTO client = new RegisteredClientDTO();
    client.setClientName("test-client-creation");
    client.setClientId("test-client-creation");
    client.setGrantTypes(Sets.newHashSet(AuthorizationGrantType.CLIENT_CREDENTIALS));
    client.setScope(Sets.newHashSet("test"));
    client.setJwk(NOT_A_JSON_STRING);

    String expectedMessage = "Invalid JSON: Unexpected token " + NOT_A_JSON_STRING + " at position 25.";

    mvc
      .perform(post(IAM_CLIENTS_API_URL).contentType(APPLICATION_JSON)
        .content(mapper.writeValueAsString(client)))
      .andExpect(BAD_REQUEST).andExpect(jsonPath("$.error", is(expectedMessage)));
  }


}
