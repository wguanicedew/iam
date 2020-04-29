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

import static it.infn.mw.iam.core.oauth.granters.TokenExchangeTokenGranter.TOKEN_EXCHANGE_GRANT_TYPE;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mitre.oauth2.model.OAuth2AccessTokenEntity;
import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.service.ScopeClaimTranslationService;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import com.google.common.collect.Maps;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;

import it.infn.mw.iam.config.IamProperties;
import it.infn.mw.iam.core.oauth.profile.iam.ClaimValueHelper;
import it.infn.mw.iam.core.oauth.profile.iam.IamJWTProfileAccessTokenBuilder;
import it.infn.mw.iam.persistence.repository.IamOAuthAccessTokenRepository;
import it.infn.mw.iam.test.util.oauth.MockOAuth2Request;

@RunWith(MockitoJUnitRunner.class)
public class IamAccessTokenBuilderTests {

  IamProperties properties = new IamProperties();

  @Mock
  IamOAuthAccessTokenRepository repo;

  @Mock
  ScopeClaimTranslationService scService;

  @Mock
  ClaimValueHelper claimValueHelper;

  @Mock
  OAuth2AccessTokenEntity tokenEntity;

  @Mock
  OAuth2Authentication authentication;

  @Spy
  MockOAuth2Request oauth2Request =
      new MockOAuth2Request("clientId", new String[] {"openid", "profile"});

  @Mock
  UserInfo userInfo;

  final Instant now = Clock.systemDefaultZone().instant();

  IamJWTProfileAccessTokenBuilder tokenBuilder;

  @Before
  public void setup() {

    tokenBuilder =
        new IamJWTProfileAccessTokenBuilder(properties, scService, claimValueHelper, repo);
    when(tokenEntity.getExpiration()).thenReturn(null);
    when(authentication.getName()).thenReturn("auth-name");
    when(authentication.getOAuth2Request()).thenReturn(oauth2Request);
    when(userInfo.getSub()).thenReturn("userinfo-sub");
    when(oauth2Request.getGrantType()).thenReturn(TOKEN_EXCHANGE_GRANT_TYPE);
    when(repo.findByTokenValue(Mockito.any())).thenReturn(Optional.empty());
  }


  @Test(expected = IllegalArgumentException.class)
  public void testMissingSubjectTokenTokenExchangeErrors() {
    try {
      tokenBuilder.buildAccessToken(tokenEntity, authentication, userInfo, now);
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage(), containsString("subject_token not found"));
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSubjectTokenNotParsable() {
    Map<String, String> paramsMap = Maps.newHashMap();
    paramsMap.put("subject_token", "3427thjdfhgejt73ja");

    oauth2Request.setRequestParameters(paramsMap);
    try {
      tokenBuilder.buildAccessToken(tokenEntity, authentication, userInfo, now);
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage(), containsString("Error parsing subject token"));
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTokenNotFoundInRepo() {
    JWTClaimsSet.Builder jwtBuilder = new JWTClaimsSet.Builder();
    jwtBuilder.subject("sub").issuer("iss");

    PlainJWT plainJwt = new PlainJWT(jwtBuilder.build());

    Map<String, String> paramsMap = Maps.newHashMap();
    paramsMap.put("subject_token", plainJwt.serialize());
    oauth2Request.setRequestParameters(paramsMap);
    try {
      tokenBuilder.buildAccessToken(tokenEntity, authentication, userInfo, now);
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage(), containsString("Subject token not found in IAM database"));
      throw e;
    }
  }

}
