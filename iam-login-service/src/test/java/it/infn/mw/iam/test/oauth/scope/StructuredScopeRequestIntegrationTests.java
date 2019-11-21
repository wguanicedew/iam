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
package it.infn.mw.iam.test.oauth.scope;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mitre.oauth2.model.SystemScope;
import org.mitre.oauth2.service.SystemScopeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import it.infn.mw.iam.IamLoginService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class})
@WebAppConfiguration
@Transactional
@TestPropertySource(properties = {
    "scope.matchers[0].name=storage.read",
    "scope.matchers[0].type=path",
    "scope.matchers[0].prefix=storage.read",
    "scope.matchers[0].path=/",
    "scope.matchers[1].name=storage.write",
    "scope.matchers[1].type=path",
    "scope.matchers[1].prefix=storage.write",
    "scope.matchers[1].path=/"
}
)
public class StructuredScopeRequestIntegrationTests {

  private static final String CLIENT_CREDENTIALS_GRANT_TYPE = "client_credentials";
  private static final String CLIENT_CREDENTIALS_CLIENT_ID = "client-cred";
  private static final String CLIENT_CREDENTIALS_CLIENT_SECRET = "secret";
  
  @Autowired
  private WebApplicationContext context;
  
  @Autowired
  SystemScopeService scopeService;
  
  private MockMvc mvc;

  @Before
  public void setup() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(context)
      .apply(springSecurity())
      .alwaysDo(log())
      .build();
    
    SystemScope storageReadScope = new SystemScope("storage.read:/");
    storageReadScope.setRestricted(true);
    
    SystemScope storageWriteScope = new SystemScope("storage.write:/");
    storageWriteScope.setRestricted(true);

    scopeService.save(storageReadScope);
    scopeService.save(storageWriteScope);
  }
  
  @Test
  public void test() throws Exception {

    // @formatter:off
    mvc.perform(post("/token")
        .with(httpBasic(CLIENT_CREDENTIALS_CLIENT_ID, CLIENT_CREDENTIALS_CLIENT_SECRET))
        .param("grant_type", CLIENT_CREDENTIALS_GRANT_TYPE)
        .param("scope", "storage.read:/a-path storage.write:/another-path"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.scope", containsString("storage.read:/a-path")))
      .andExpect(jsonPath("$.scope", containsString("storage.write:/another-path")));
    // @formatter:on
  }

}
