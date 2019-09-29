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
package it.infn.mw.iam.core.oauth.profile.iam;

import static com.google.common.base.Strings.isNullOrEmpty;
import static it.infn.mw.iam.core.oauth.ClaimValueHelper.ADDITIONAL_CLAIMS;
import static java.util.stream.Collectors.joining;

import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import org.mitre.oauth2.model.OAuth2AccessTokenEntity;
import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.service.ScopeClaimTranslationService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import com.google.common.collect.Lists;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;

import it.infn.mw.iam.config.IamProperties;
import it.infn.mw.iam.core.oauth.ClaimValueHelper;
import it.infn.mw.iam.core.oauth.profile.JWTAccessTokenBuilder;
import it.infn.mw.iam.persistence.repository.UserInfoAdapter;

public class IamJWTProfileAccessTokenBuilder implements JWTAccessTokenBuilder {
  private static final String AUD_KEY = "aud";
  private static final String SCOPE_CLAIM_NAME = "scope";
  private static final String SPACE = " ";

  final IamProperties properties;
  final ScopeClaimTranslationService scopeClaimConverter;
  final ClaimValueHelper claimValueHelper;

  public IamJWTProfileAccessTokenBuilder(IamProperties properties,
      ScopeClaimTranslationService scopeClaimConverter, ClaimValueHelper claimValueHelper) {
    this.properties = properties;
    this.scopeClaimConverter = scopeClaimConverter;
    this.claimValueHelper = claimValueHelper;
  }

  @Override
  public JWTClaimsSet buildAccessToken(OAuth2AccessTokenEntity token,
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
      builder.audience(Lists.newArrayList(audience));
    }


    if (properties.getAccessToken().isIncludeAuthnInfo() && userInfo != null) {
      Set<String> requiredClaims = scopeClaimConverter.getClaimsForScopeSet(token.getScope());

      requiredClaims.stream()
        .filter(ADDITIONAL_CLAIMS::contains)
        .forEach(c -> builder.claim(c, claimValueHelper.getClaimValueFromUserInfo(c,
            ((UserInfoAdapter) userInfo).getUserinfo())));
    }

    if (properties.getAccessToken().isIncludeScope() && !token.getScope().isEmpty()) {
      builder.claim(SCOPE_CLAIM_NAME, token.getScope().stream().collect(joining(SPACE)));
    }

    if (properties.getAccessToken().isIncludeNbf()) {
      builder.notBeforeTime(Date.from(issueTime));
    }

    return builder.build();
  }

}
