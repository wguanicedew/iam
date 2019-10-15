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

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

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
@TestPropertySource(properties = {"iam.jwt-profile.default-profile=wlcg"})
public class WLCGProfileIntegrationTests extends EndpointsTestUtils {

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
    mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
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

}
