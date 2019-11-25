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
package it.infn.mw.iam.core.oauth;

import java.util.Map;
import java.util.Set;

import org.mitre.oauth2.service.ClientDetailsEntityService;
import org.mitre.openid.connect.request.ConnectOAuth2RequestFactory;
import org.mitre.openid.connect.web.AuthenticationTimeStamper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;

import com.google.common.base.Joiner;

import it.infn.mw.iam.core.oauth.profile.JWTProfileResolver;
import it.infn.mw.iam.core.oauth.scope.pdp.IamScopeFilter;

public class IamOAuth2RequestFactory extends ConnectOAuth2RequestFactory {

  public static final Logger LOG = LoggerFactory.getLogger(IamOAuth2RequestFactory.class);

  protected static final String[] AUDIENCE_KEYS = {"aud", "audience"};
  public static final String AUD = "aud";
  public static final String PASSWORD_GRANT = "password";

  private final IamScopeFilter scopeFilter;

  private final JWTProfileResolver profileResolver;

  private final Joiner joiner = Joiner.on(' ');

  public IamOAuth2RequestFactory(ClientDetailsEntityService clientDetailsService,
      IamScopeFilter scopeFilter, JWTProfileResolver profileResolver) {
    super(clientDetailsService);
    this.scopeFilter = scopeFilter;
    this.profileResolver = profileResolver;
  }


  @Override
  public AuthorizationRequest createAuthorizationRequest(Map<String, String> inputParams) {

    Authentication authn = SecurityContextHolder.getContext().getAuthentication();

    if (authn != null && !(authn instanceof AnonymousAuthenticationToken)) {
      final Set<String> requestedScopes =
          OAuth2Utils.parseParameterList(inputParams.get(OAuth2Utils.SCOPE));

      scopeFilter.filterScopes(requestedScopes, authn);
      inputParams.put(OAuth2Utils.SCOPE, joiner.join(requestedScopes));
    }
    
    AuthorizationRequest authzRequest = super.createAuthorizationRequest(inputParams);
    
    for (String audienceKey : AUDIENCE_KEYS) {
      if (inputParams.containsKey(audienceKey)) {
        if (!authzRequest.getExtensions().containsKey(AUD)) {
          authzRequest.getExtensions().put(AUD, inputParams.get(audienceKey));
        }
        
        break;
      }
    }

    return authzRequest;

  }

  private void handlePasswordGrantAuthenticationTimestamp(OAuth2Request request) {
    if (PASSWORD_GRANT.equals(request.getGrantType())) {
      String now = Long.toString(System.currentTimeMillis());
      request.getExtensions().put(AuthenticationTimeStamper.AUTH_TIMESTAMP, now);
    }
  }

  /**
   * This implementation extends what's already done by MitreID implementation with audience request
   * parameter handling (both "aud" and "audience" are accepted).
   *
   * 
   */
  @Override
  public OAuth2Request createOAuth2Request(ClientDetails client, TokenRequest tokenRequest) {

    OAuth2Request request = super.createOAuth2Request(client, tokenRequest);

    handlePasswordGrantAuthenticationTimestamp(request);

    for (String audienceKey : AUDIENCE_KEYS) {
      if (tokenRequest.getRequestParameters().containsKey(audienceKey)) {

        if (!request.getExtensions().containsKey(AUD)) {
          request.getExtensions().put(AUD, tokenRequest.getRequestParameters().get(audienceKey));
        }

        break;
      }
    }

    profileResolver.resolveProfile(client.getClientId())
      .getRequestValidator()
      .validateRequest(request);

    return request;
  }

}
