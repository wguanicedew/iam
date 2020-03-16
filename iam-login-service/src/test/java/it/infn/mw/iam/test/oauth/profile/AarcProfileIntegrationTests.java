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
package it.infn.mw.iam.test.oauth.profile;

import static it.infn.mw.iam.core.oauth.profile.aarc.AarcJWTProfile.AARC_OIDC_CLAIM_AFFILIATION;
import static it.infn.mw.iam.core.oauth.profile.aarc.AarcJWTProfile.AARC_OIDC_CLAIM_ENTITLEMENT;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.google.common.collect.Lists;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.config.IamProperties;
import it.infn.mw.iam.test.core.CoreControllerTestSupport;
import it.infn.mw.iam.test.oauth.EndpointsTestUtils;
import it.infn.mw.iam.test.util.WithAnonymousUser;
import it.infn.mw.iam.test.util.WithMockOAuthUser;
import it.infn.mw.iam.test.util.oauth.MockOAuth2Filter;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class, CoreControllerTestSupport.class})
@WebAppConfiguration
@Transactional
@TestPropertySource(properties = {
    // @formatter:off
    "iam.jwt-profile.default-profile=aarc",
    "iam.organisation.name=org",
    "iam.urn.namespace=geant:iam:test",
    // @formatter:on
})
public class AarcProfileIntegrationTests extends EndpointsTestUtils {

  private static final String CLIENT_ID = "password-grant";
  private static final String CLIENT_SECRET = "secret";
  private static final String USERNAME = "test";
  private static final String PASSWORD = "password";

  private static final String URN_GROUP_ANALYSIS = "urn:geant:iam:test:group:Analysis#org";
  private static final String URN_GROUP_PRODUCTION = "urn:geant:iam:test:group:Production#org";

  @Autowired
  private WebApplicationContext context;

  @Autowired
  IamProperties iamProperties;

  @Autowired
  MockOAuth2Filter oauth2Filter;

  @Before
  public void setup() {
    mvc =
        MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).alwaysDo(log()).build();
  }

  @After
  public void teardown() {
    oauth2Filter.cleanupSecurityContext();
  }

  private String getAccessTokenForUser(String scopes) throws Exception {

    return new AccessTokenGetter().grantType("password")
      .clientId(CLIENT_ID)
      .clientSecret(CLIENT_SECRET)
      .username(USERNAME)
      .password(PASSWORD)
      .scope(scopes)
      .getAccessTokenValue();
  }

  @Test
  @WithAnonymousUser
  public void testAarcProfile() throws Exception {
    JWT token = JWTParser.parse(getAccessTokenForUser("openid profile"));

    assertThat(token.getJWTClaimsSet().getClaim("groups"), nullValue());
    List<String> groups = Lists.newArrayList(token.getJWTClaimsSet().getStringArrayClaim(AARC_OIDC_CLAIM_ENTITLEMENT));
    assertThat(groups, hasSize(2));
    assertThat(groups, hasItem(URN_GROUP_ANALYSIS));
    assertThat(groups, hasItem(URN_GROUP_PRODUCTION));

  }

  @Test
  @WithAnonymousUser
  public void testAarcProfileIntrospect() throws Exception {
    JWT token = JWTParser.parse(getAccessTokenForUser("openid profile"));

    // @formatter:off
    mvc.perform(post("/introspect")
        .with(httpBasic(CLIENT_ID, CLIENT_SECRET))
        .param("token", token.getParsedString()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.active", equalTo(true)))
      .andExpect(jsonPath("$." + AARC_OIDC_CLAIM_AFFILIATION, equalTo("org")))
      .andExpect(jsonPath("$." + AARC_OIDC_CLAIM_ENTITLEMENT, hasSize(equalTo(2))))
      .andExpect(jsonPath("$." + AARC_OIDC_CLAIM_ENTITLEMENT, containsInAnyOrder(URN_GROUP_ANALYSIS, URN_GROUP_PRODUCTION)))
      .andExpect(jsonPath("$.name", equalTo("Test User")))
      .andExpect(jsonPath("$.given_name", equalTo("Test")))
      .andExpect(jsonPath("$.family_name", equalTo("User")))
      .andExpect(jsonPath("$.email", equalTo("test@iam.test")));
    // @formatter:on

  }

  @Test
  @WithMockOAuthUser(clientId = CLIENT_ID, user = USERNAME, authorities = {"ROLE_USER"},
      scopes = {"openid profile"})
  public void testAarcProfileUserinfo() throws Exception {

    // @formatter:off
    mvc.perform(get("/userinfo"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.sub").exists())
      .andExpect(jsonPath("$.organisation_name").doesNotExist())
      .andExpect(jsonPath("$.groups").doesNotExist())
      .andExpect(jsonPath("$." + AARC_OIDC_CLAIM_AFFILIATION, equalTo("org")))
      .andExpect(jsonPath("$." + AARC_OIDC_CLAIM_ENTITLEMENT, hasSize(equalTo(2))))
      .andExpect(jsonPath("$." + AARC_OIDC_CLAIM_ENTITLEMENT, containsInAnyOrder(URN_GROUP_ANALYSIS, URN_GROUP_PRODUCTION)))
      .andExpect(jsonPath("$.name", equalTo("Test User")))
      .andExpect(jsonPath("$.given_name", equalTo("Test")))
      .andExpect(jsonPath("$.family_name", equalTo("User")));
    // @formatter:on
  }

  @Test
  @WithMockOAuthUser(clientId = CLIENT_ID, user = USERNAME, authorities = {"ROLE_USER"},
      scopes = {"openid profile email"})
  public void testAarcProfileUserinfoWithEmail() throws Exception {

    // @formatter:off
    mvc.perform(get("/userinfo"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.sub").exists())
      .andExpect(jsonPath("$.organisation_name").doesNotExist())
      .andExpect(jsonPath("$.groups").doesNotExist())
      .andExpect(jsonPath("$." + AARC_OIDC_CLAIM_AFFILIATION, equalTo("org")))
      .andExpect(jsonPath("$." + AARC_OIDC_CLAIM_ENTITLEMENT, hasSize(equalTo(2))))
      .andExpect(jsonPath("$." + AARC_OIDC_CLAIM_ENTITLEMENT, containsInAnyOrder(URN_GROUP_ANALYSIS, URN_GROUP_PRODUCTION)))
      .andExpect(jsonPath("$.name", equalTo("Test User")))
      .andExpect(jsonPath("$.given_name", equalTo("Test")))
      .andExpect(jsonPath("$.family_name", equalTo("User")))
      .andExpect(jsonPath("$.email", equalTo("test@iam.test")))
      .andExpect(jsonPath("$.email_verified", equalTo(true)));
    // @formatter:on
  }

}
