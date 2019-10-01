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

import static java.util.stream.Collectors.joining;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.mitre.oauth2.model.OAuth2AccessTokenEntity;
import org.mitre.oauth2.service.IntrospectionResultAssembler;
import org.mitre.openid.connect.model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import it.infn.mw.iam.config.IamProperties;
import it.infn.mw.iam.core.oauth.profile.IntrospectionResultHelper;
import it.infn.mw.iam.persistence.model.IamGroup;
import it.infn.mw.iam.persistence.model.IamUserInfo;
import it.infn.mw.iam.persistence.repository.UserInfoAdapter;

public class IamJWTProfileTokenIntrospectionHelper implements IntrospectionResultHelper {

  public static final Logger LOG =
      LoggerFactory.getLogger(IamJWTProfileTokenIntrospectionHelper.class);

  public static final String PROFILE = "profile";
  public static final String AUDIENCE = "aud";
  public static final String NAME = "name";
  public static final String PREFERRED_USERNAME = "preferred_username";
  public static final String EMAIL = "email";
  public static final String GROUPS = "groups";
  public static final String ORGANISATION_NAME = "organisation_name";
  public static final String ISSUER = "iss";

  private IamProperties properties;
  private IntrospectionResultAssembler assembler;

  public IamJWTProfileTokenIntrospectionHelper(IamProperties props,
      IntrospectionResultAssembler assembler) {
    this.properties = props;
    this.assembler = assembler;
  }

  @Override
  public Map<String, Object> assembleIntrospectionResult(OAuth2AccessTokenEntity accessToken,
      UserInfo userInfo, Set<String> authScopes) {

    Map<String, Object> result = assembler.assembleFrom(accessToken, userInfo, authScopes);

    final String oidcIssuer = properties.getIssuer();
    String trailingSlashIssuer = oidcIssuer.endsWith("/") ? oidcIssuer : oidcIssuer + "/";

    result.put(ISSUER, trailingSlashIssuer);

    try {

      List<String> audience = accessToken.getJwt().getJWTClaimsSet().getAudience();

      if (audience != null && !audience.isEmpty()) {
        result.put(AUDIENCE, audience.stream().collect(joining(" ")));
      }

    } catch (ParseException e) {
      LOG.error("Error getting audience out of access token: {}", e.getMessage(), e);
    }

    // Intersection of scopes authorized for the client and scopes linked to the
    // access token
    Set<String> scopes = Sets.intersection(authScopes, accessToken.getScope());

    if (userInfo != null) {
      if (scopes.contains(PROFILE)) {

        IamUserInfo iamUserInfo = ((UserInfoAdapter) userInfo).getUserinfo();

        if (!iamUserInfo.getGroups().isEmpty()) {

          result.put(GROUPS,
              iamUserInfo.getGroups().stream().map(IamGroup::getName).collect(Collectors.toList()));
        }

        result.put(NAME, iamUserInfo.getName());
        result.put(PREFERRED_USERNAME, iamUserInfo.getPreferredUsername());
        result.put(ORGANISATION_NAME, properties.getOrganisation().getName());
      }

      if (scopes.contains(EMAIL)) {
        result.put(EMAIL, userInfo.getEmail());
      }
    }

    return result;
  }

}
