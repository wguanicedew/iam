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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.config.IamProperties;
import it.infn.mw.iam.test.core.CoreControllerTestSupport;
import it.infn.mw.iam.test.oauth.EndpointsTestUtils;
import it.infn.mw.iam.test.util.WithAnonymousUser;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class, CoreControllerTestSupport.class})
@WebAppConfiguration
@Transactional
@TestPropertySource(properties = {
    // @formatter:off
    "iam.jwt-profile.default-profile=wlcg",
    "scope.matchers[0].name=storage.read",
    "scope.matchers[0].type=path",
    "scope.matchers[0].prefix=storage.read",
    "scope.matchers[0].path=/",
    "scope.matchers[1].name=storage.write",
    "scope.matchers[1].type=path",
    "scope.matchers[1].prefix=storage.write",
    "scope.matchers[1].path=/"
    // @formatter:on
})
public class WLCGProfileIntegrationTests extends EndpointsTestUtils {

  private static final String CLIENT_CREDENTIALS_GRANT_TYPE = "client_credentials";
  private static final String CLIENT_CREDENTIALS_CLIENT_ID = "client-cred";
  private static final String CLIENT_CREDENTIALS_CLIENT_SECRET = "secret";

  private static final String CLIENT_ID = "password-grant";
  private static final String CLIENT_SECRET = "secret";
  private static final String USERNAME = "test";
  private static final String PASSWORD = "password";

  @Autowired
  private WebApplicationContext context;

  @Autowired
  IamProperties iamProperties;

  @Before
  public void setup() {
    mvc =
        MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).alwaysDo(log()).build();
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
  public void testWlcgProfile() throws Exception {
    JWT token = JWTParser.parse(getAccessTokenForUser("openid profile"));

    assertThat(token.getJWTClaimsSet().getClaim("scope"), is("openid profile"));
    assertThat(token.getJWTClaimsSet().getClaim("wlcg.ver"), is("1.0"));
    assertThat(token.getJWTClaimsSet().getClaim("groups"), nullValue());
    assertThat(token.getJWTClaimsSet().getClaim("wlcg.groups"), nullValue());
  }

  @Test
  @WithAnonymousUser
  public void testWlcgProfileGroups() throws Exception {
    JWT token = JWTParser.parse(getAccessTokenForUser("openid profile wlcg.groups"));

    assertThat(token.getJWTClaimsSet().getClaim("scope"), is("openid profile wlcg.groups"));
    assertThat(token.getJWTClaimsSet().getClaim("wlcg.ver"), is("1.0"));
    assertThat(token.getJWTClaimsSet().getClaim("groups"), nullValue());
    assertThat(token.getJWTClaimsSet().getStringListClaim("wlcg.groups"),
        hasItems("/Production", "/Analysis"));
  }

  @Test
  public void testWlcgProfileClientCredentials() throws Exception {

    mvc
      .perform(post("/token")
        .with(httpBasic(CLIENT_CREDENTIALS_CLIENT_ID, CLIENT_CREDENTIALS_CLIENT_SECRET))
        .param("grant_type", CLIENT_CREDENTIALS_GRANT_TYPE)
        .param("scope", "storage.read:/a-path storage.write:/another-path"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.scope", containsString("storage.read:/a-path")))
      .andExpect(jsonPath("$.scope", containsString("storage.write:/another-path")));
  }

  @Test
  public void testWlcgProfileGroupRequestClientCredentials() throws Exception {

    String response = mvc
      .perform(post("/token")
        .with(httpBasic(CLIENT_CREDENTIALS_CLIENT_ID, CLIENT_CREDENTIALS_CLIENT_SECRET))
        .param("grant_type", CLIENT_CREDENTIALS_GRANT_TYPE)
        .param("scope", "storage.read:/a-path wlcg.groups"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.scope", containsString("storage.read:/a-path")))
      .andExpect(jsonPath("$.scope", containsString("wlcg.groups")))
      .andReturn()
      .getResponse()
      .getContentAsString();

    
    DefaultOAuth2AccessToken tokenResponseObject =
        mapper.readValue(response, DefaultOAuth2AccessToken.class);
    
    JWT accessToken = JWTParser.parse(tokenResponseObject.getValue());
    
    assertThat(accessToken.getJWTClaimsSet().getClaim("wlcg.groups"), nullValue());

  }
}
