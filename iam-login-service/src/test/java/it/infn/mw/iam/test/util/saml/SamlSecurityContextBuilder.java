/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2018
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
package it.infn.mw.iam.test.util.saml;

import static org.mockito.Mockito.when;

import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.providers.ExpiringUsernameAuthenticationToken;
import org.springframework.security.saml.SAMLCredential;

import com.google.common.base.Strings;

import it.infn.mw.iam.authn.saml.SamlExternalAuthenticationToken;
import it.infn.mw.iam.authn.saml.util.Saml2Attribute;
import it.infn.mw.iam.authn.saml.util.SamlAttributeNames;
import it.infn.mw.iam.persistence.model.IamSamlId;
import it.infn.mw.iam.test.ext_authn.saml.SamlAuthenticationTestSupport;
import it.infn.mw.iam.test.util.SecurityContextBuilderSupport;

public class SamlSecurityContextBuilder extends SecurityContextBuilderSupport {

  SAMLCredential samlCredential;

  String subjectAttribute = SamlAttributeNames.eduPersonUniqueId;

  public SamlSecurityContextBuilder() {
    samlCredential = Mockito.mock(SAMLCredential.class);
    issuer = SamlAuthenticationTestSupport.DEFAULT_IDP_ID;
    subject = "test-saml-user";
  }

  public SamlSecurityContextBuilder samlAttribute(Saml2Attribute attribute, String attributeValue) {
    when(samlCredential.getAttributeAsString(attribute.getAttributeName()))
      .thenReturn(attributeValue);
    return this;
  }

  public SamlSecurityContextBuilder subjectAttribute(String subjectAttr) {
    this.subjectAttribute = subjectAttr;
    return this;
  }

  @Override
  public SecurityContextBuilderSupport email(String email) {
    when(samlCredential.getAttributeAsString(SamlAttributeNames.mail)).thenReturn(email);
    return this;
  }

  @Override
  public SecurityContextBuilderSupport name(String givenName, String familyName) {

    if (!Strings.isNullOrEmpty(givenName) && Strings.isNullOrEmpty(familyName)) {
      when(samlCredential.getAttributeAsString(SamlAttributeNames.givenName)).thenReturn(givenName);
      when(samlCredential.getAttributeAsString(SamlAttributeNames.sn)).thenReturn(familyName);
    }

    return this;
  }

  @Override
  public SecurityContext buildSecurityContext() {
    SecurityContext context = SecurityContextHolder.createEmptyContext();

    when(samlCredential.getRemoteEntityID()).thenReturn(issuer);

    when(samlCredential.getAttributeAsString(subjectAttribute)).thenReturn(subject);

    ExpiringUsernameAuthenticationToken samlToken = new ExpiringUsernameAuthenticationToken(
        expirationTime, subject, samlCredential, authorities);

    IamSamlId samlId = new IamSamlId(issuer, subjectAttribute, subject);

    SamlExternalAuthenticationToken token = new SamlExternalAuthenticationToken(samlId, samlToken,
        samlToken.getTokenExpiration(), subject, samlToken.getCredentials(), authorities);

    context.setAuthentication(token);

    return context;

  }

}
