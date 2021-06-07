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

import org.mitre.oauth2.model.ClientDetailsEntity;
import org.mitre.oauth2.model.OAuth2AccessTokenEntity;
import org.mitre.openid.connect.service.impl.DefaultOIDCTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.stereotype.Service;

import com.nimbusds.jwt.JWTClaimsSet.Builder;

import it.infn.mw.iam.api.common.NoSuchAccountError;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

@Service
@Primary
public class IamOIDCTokenService extends DefaultOIDCTokenService {


  public static final Logger LOG = LoggerFactory.getLogger(IamOIDCTokenService.class);

  @Autowired
  private JWTProfileResolver profileResolver;

  @Autowired
  private IamAccountRepository accountRepository;

  @Autowired
  public IamOIDCTokenService(JWTProfileResolver resolver, IamAccountRepository accountRepository) {
    this.profileResolver = resolver;
    this.accountRepository = accountRepository;
  }

  @Override
  protected void addCustomIdTokenClaims(Builder idClaims, ClientDetailsEntity client,
      OAuth2Request request, String sub, OAuth2AccessTokenEntity accessToken) {

    IamAccount account =
        accountRepository.findByUuid(sub).orElseThrow(() -> NoSuchAccountError.forUuid(sub));

    JWTProfile profile = profileResolver.resolveProfile(client.getClientId());

    profile.getIDTokenCustomizer()
      .customizeIdTokenClaims(idClaims, client, request, sub, accessToken, account);
  }

}
