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
package it.infn.mw.iam.core.oauth.profile.common;

import static com.google.common.base.Strings.isNullOrEmpty;
import static it.infn.mw.iam.core.oauth.granters.TokenExchangeTokenGranter.TOKEN_EXCHANGE_GRANT_TYPE;
import static java.util.Objects.isNull;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.mitre.oauth2.model.OAuth2AccessTokenEntity;
import org.mitre.openid.connect.model.UserInfo;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;

import it.infn.mw.iam.config.IamProperties;
import it.infn.mw.iam.core.oauth.granters.TokenExchangeTokenGranter;
import it.infn.mw.iam.core.oauth.profile.JWTAccessTokenBuilder;
import net.minidev.json.JSONObject;

public abstract class BaseAccessTokenBuilder implements JWTAccessTokenBuilder {

  public static final String AUDIENCE = "audience";
  public static final String AUD_KEY = "aud";
  public static final String SCOPE_CLAIM_NAME = "scope";
  public static final String ACT_CLAIM_NAME = "act";
  public static final String SPACE = " ";


  protected final IamProperties properties;

  protected final Splitter SPLITTER = Splitter.on(' ').trimResults().omitEmptyStrings();

  public BaseAccessTokenBuilder(IamProperties properties) {
    this.properties = properties;
  }


  protected JWTClaimsSet.Builder handleClientTokenExchange(JWTClaimsSet.Builder builder,
      OAuth2AccessTokenEntity token, OAuth2Authentication authentication, UserInfo userInfo) {

    if (TOKEN_EXCHANGE_GRANT_TYPE.equals(authentication.getOAuth2Request().getGrantType())) {
      
      if (authentication.isClientOnly()) {
        String subjectClientId = (String) authentication.getOAuth2Request()
          .getExtensions()
          .get(TokenExchangeTokenGranter.TOKEN_EXCHANGE_SUBJECT_CLIENT_ID_KEY);

        authentication.getOAuth2Request()
        .getExtensions().remove(TokenExchangeTokenGranter.TOKEN_EXCHANGE_SUBJECT_CLIENT_ID_KEY);
        
        builder.subject(subjectClientId);
      }

      Map<String, Object> actClaimContent = Maps.newHashMap();

      actClaimContent.put("sub", authentication.getOAuth2Request().getClientId());
      
      JSONObject subjectTokenActClaim = (JSONObject) authentication.getOAuth2Request()
        .getExtensions()
        .get(TokenExchangeTokenGranter.TOKEN_EXCHANGE_SUBJECT_ACT_KEY);

      if (!isNull(subjectTokenActClaim)) {
        actClaimContent.put("act", subjectTokenActClaim);
        authentication.getOAuth2Request()
        .getExtensions().remove(TokenExchangeTokenGranter.TOKEN_EXCHANGE_SUBJECT_ACT_KEY);
      }

      builder.claim(ACT_CLAIM_NAME, actClaimContent);
    }

    return builder;
  }

  protected boolean hasRefreshTokenAudienceRequest(OAuth2Authentication authentication) {
    if (!isNull(authentication.getOAuth2Request().getRefreshTokenRequest())) {
      final String audience = authentication.getOAuth2Request()
        .getRefreshTokenRequest()
        .getRequestParameters()
        .get(AUDIENCE);
      return !isNullOrEmpty(audience);
    }
    return false;
  }

  protected boolean hasAudienceRequest(OAuth2Authentication authentication) {
    final String audience = (String) authentication.getOAuth2Request().getExtensions().get(AUD_KEY);
    return !isNullOrEmpty(audience);
  }

  protected JWTClaimsSet.Builder baseJWTSetup(OAuth2AccessTokenEntity token,
      OAuth2Authentication authentication, UserInfo userInfo, Instant issueTime) {

    String subject = null;

    if (userInfo == null) {
      subject = authentication.getName();
    } else {
      subject = userInfo.getSub();
    }

    Builder builder = new JWTClaimsSet.Builder().issuer(properties.getIssuer())
      .issueTime(Date.from(issueTime))
      .expirationTime(token.getExpiration())
      .subject(subject)
      .jwtID(UUID.randomUUID().toString());

    String audience = null;
    if (hasAudienceRequest(authentication)) {
      audience = (String) authentication.getOAuth2Request().getExtensions().get(AUD_KEY);
    }

    if (hasRefreshTokenAudienceRequest(authentication)) {
      audience = authentication.getOAuth2Request()
        .getRefreshTokenRequest()
        .getRequestParameters()
        .get(AUDIENCE);
    }

    if (!isNullOrEmpty(audience)) {
      builder.audience(SPLITTER.splitToList(audience));
    }
    return handleClientTokenExchange(builder, token, authentication, userInfo);
  }


}
