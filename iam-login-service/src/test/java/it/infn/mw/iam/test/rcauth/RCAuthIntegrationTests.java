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
package it.infn.mw.iam.test.rcauth;


import static it.infn.mw.iam.rcauth.DefaultRcAuthRequestService.RCAUTH_CTXT_SESSION_KEY;
import static it.infn.mw.iam.rcauth.RCAuthController.GETCERT_PATH;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.config.saml.IamProperties;
import it.infn.mw.iam.test.util.WithAnonymousUser;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class, RCAuthTestSupport.class})
@WebAppConfiguration
@Transactional
@TestPropertySource(
    properties = {"rcauth.enabled=true", "rcauth.client-id=" + RCAuthTestSupport.CLIENT_ID,
        "rcauth.client-secret=" + RCAuthTestSupport.CLIENT_SECRET,
        "rcauth.issuer=" + RCAuthTestSupport.ISSUER})
public class RCAuthIntegrationTests extends RCAuthTestSupport {

  @Autowired
  private WebApplicationContext context;

  @Autowired
  IamProperties iamProperties;

  private MockMvc mvc;

  @Before
  public void setup() {
    mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
  }

  @Test
  @WithAnonymousUser
  public void rcAuthRequiresAuthenticatedUser() throws Exception {
    mvc.perform(get(GETCERT_PATH).with(csrf().asHeader()))
      .andExpect(status().isFound())
      .andExpect(redirectedUrl(LOGIN_URL));
  }

  @Test
  @WithMockUser(username = "test")
  public void rcAuthWithUserSucceedsAndSetsSessionAttributes() throws Exception {

    MvcResult result = mvc.perform(get(GETCERT_PATH).with(csrf().asHeader()))
      .andExpect(status().isFound())
      .andExpect(request().sessionAttribute(RCAUTH_CTXT_SESSION_KEY,
          notNullValue()))
      .andReturn();

    UriComponents uc =
        UriComponentsBuilder.fromHttpUrl(result.getResponse().getRedirectedUrl()).build();

    assertThat(uc.getHost(), is(RCAUTH_HOST));
  }
  
}
