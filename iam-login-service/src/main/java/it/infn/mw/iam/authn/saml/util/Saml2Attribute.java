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
package it.infn.mw.iam.authn.saml.util;

public enum Saml2Attribute {

  EPUID("eduPersonUniqueId", "urn:oid:1.3.6.1.4.1.5923.1.1.1.13"),
  EPTID("eduPersonTargetedId", "urn:oid:1.3.6.1.4.1.5923.1.1.1.10"),
  EPPN("eduPersonPrincipalName", "urn:oid:1.3.6.1.4.1.5923.1.1.1.6"),
  EPORCID("eduPersonOrcid", "urn:oid:1.3.6.1.4.1.5923.1.1.1.16"),
  MAIL("mail", "urn:oid:0.9.2342.19200300.100.1.3"),
  GIVEN_NAME("givenName", "urn:oid:2.5.4.42"),
  SN("sn", "urn:oid:2.5.4.4"),
  CN("cn", "urn:oid:2.5.4.3"),
  EMPLOYEE_NUMBER("employeeNumber", "urn:oid:2.16.840.1.113730.3.1.3"),
  SPID_CODE("spidCode", "spidCode"),
  SUBJECT_ID("subjectId", "urn:oasis:names:tc:SAML:profile:subject-id"),
  PAIRWISE_ID("pairwiseId", "urn:oasis:names:tc:SAML:profile:pairwise-id");

  private String alias;
  private String attributeName;
  
  private Saml2Attribute(String alias, String attributeName) {
    this.alias = alias;
    this.attributeName = attributeName;
  }
  
  public String getAlias() {
    return alias;
  }

  public String getAttributeName() {
    return attributeName;
  }
  
}
