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

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.mitre.oauth2.model.OAuth2AccessTokenEntity;
import org.mitre.openid.connect.model.UserInfo;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import com.google.common.base.Splitter;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;

import it.infn.mw.iam.config.IamProperties;
import it.infn.mw.iam.core.oauth.profile.JWTAccessTokenBuilder;

public abstract class BaseAccessTokenBuilder implements JWTAccessTokenBuilder {

  public static final String AUD_KEY = "aud";
  public static final String SCOPE_CLAIM_NAME = "scope";
  public static final String SPACE = " ";

  protected final IamProperties properties;
  
  protected final Splitter SPLITTER = Splitter.on(' ').trimResults().omitEmptyStrings();

  public BaseAccessTokenBuilder(IamProperties properties) {
    this.properties = properties;
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

    final String audience = (String) authentication.getOAuth2Request().getExtensions().get(AUD_KEY);

    if (!isNullOrEmpty(audience)) {
      builder.audience(SPLITTER.splitToList(audience));
    }

    return builder;
  }


}
