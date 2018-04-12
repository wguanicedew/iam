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

import com.google.common.collect.ImmutableMap;

public class SamlIdResolvers {

  public static final String NAME_ID_NAME = "nameID";
  
  private ImmutableMap<String, SamlUserIdentifierResolver> registeredResolvers;

  public SamlIdResolvers() {
    ImmutableMap.Builder<String, SamlUserIdentifierResolver> builder =
        ImmutableMap.<String, SamlUserIdentifierResolver>builder();

    for (Saml2Attribute a : Saml2Attribute.values()) {
      builder.put(a.getAlias(), new AttributeUserIdentifierResolver(a));
    }

    builder.put(NAME_ID_NAME, new NameIdUserIdentifierResolver());

    registeredResolvers = builder.build();
  }

  public SamlUserIdentifierResolver byAttribute(Saml2Attribute attribute) {
    return registeredResolvers.get(attribute.getAlias());
  }
  
  public SamlUserIdentifierResolver byName(String name) {
    return registeredResolvers.get(name);
  }


}
