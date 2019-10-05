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
package it.infn.mw.iam.core.oauth.profile;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.isNull;

import java.util.Map;
import java.util.Optional;

import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;

import it.infn.mw.iam.api.scim.exception.IllegalArgumentException;

public class ScopeAwareProfileResolver implements JWTProfileResolver {

  public static final String PROFILE_SELECTOR_SCOPE = "urn:indigo-iam.github.io:jwt-profile#";

  public static final String IAM_PROFILE_ID = profileId("iam");
  public static final String WLCG_PROFILE_ID = profileId("wlcg-1.0");

  private final Map<String, JWTProfile> profileMap;
  private final JWTProfile defaultProfile;
  private final ClientDetailsService clientDetailsService;
  

  public ScopeAwareProfileResolver(JWTProfile defaultProfile, Map<String, JWTProfile> profileMap,
      ClientDetailsService clientDetailsService) {
    this.defaultProfile = defaultProfile;
    this.profileMap = profileMap;
    this.clientDetailsService = clientDetailsService;
  }

  private Optional<String> findProfileScope(ClientDetails client) {
    return client.getScope().stream().filter(s -> s.startsWith(PROFILE_SELECTOR_SCOPE)).findFirst();
  }


  @Override
  public JWTProfile resolveProfile(String clientId) {
    checkArgument(!isNullOrEmpty(clientId), "non-null clientId required");
    ClientDetails client = clientDetailsService.loadClientByClientId(clientId);

    if (isNull(client)) {
      throw new IllegalArgumentException("Client not found: " + clientId);
    }

    return findProfileScope(client).map(profileMap::get).orElse(defaultProfile);
  }

  public static final String profileId(String profileName) {
    return String.format("%s%s", PROFILE_SELECTOR_SCOPE, profileName);
  }
}
