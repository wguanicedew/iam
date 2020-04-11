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
package it.infn.mw.iam.api.registration.cern;

import static java.util.Objects.isNull;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import it.infn.mw.iam.api.registration.cern.error.InsufficientAuthenticationError;
import it.infn.mw.iam.api.scim.exception.IllegalArgumentException;
import it.infn.mw.iam.authn.oidc.OidcExternalAuthenticationToken;
import it.infn.mw.iam.config.cern.CernProperties;

@Component
@Profile("cern")
public class DefaultCernPersonIdResolver implements CernPersonIdResolver {

  final CernProperties properties;

  @Autowired
  public DefaultCernPersonIdResolver(CernProperties properties) {
    this.properties = properties;
  }

  @Override
  public String resolvePersonId(OidcExternalAuthenticationToken token) {

    String personId = null;

    try {
      Object personIdObject = token.getExternalAuthentication()
        .getIdToken()
        .getJWTClaimsSet()
        .getClaim(properties.getPersonIdClaim());

      if (isNull(personIdObject)) {
        throw new InsufficientAuthenticationError("CERN person id not found in CERN SSO id token!");
      }

      if (personIdObject instanceof Number) {
        personId = String.valueOf(personIdObject);
      } else if (personIdObject instanceof String) {
        personId = (String) personIdObject;
      } else {
        throw new IllegalArgumentException(
            "Invalid type for personId: " + personIdObject.getClass().getName());
      }
    } catch (ParseException e) {
      throw new InsufficientAuthenticationError("CERN person id not found in CERN SSO id token!",
          e);
    }

    return personId;
  }

}
