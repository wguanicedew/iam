package it.infn.mw.iam.authn.saml.util;

import com.google.common.collect.ImmutableMap;

public class SamlIdResolvers {

  public static final String NAME_ID_NAME = "nameID";
  
  private ImmutableMap<String, SamlUserIdentifierResolver> registeredResolvers;

  public SamlIdResolvers() {
    ImmutableMap.Builder<String, SamlUserIdentifierResolver> builder =
        ImmutableMap.<String, SamlUserIdentifierResolver>builder();

    for (Saml2Attribute a : Saml2Attribute.values()) {
      builder.put(a.name(), new AttributeUserIdentifierResolver(a));
    }

    builder.put(NAME_ID_NAME, new NameIdUserIdentifierResolver());

    registeredResolvers = builder.build();
  }

  public SamlUserIdentifierResolver byAttribute(Saml2Attribute attribute) {
    return registeredResolvers.get(attribute.name());
  }
  
  public SamlUserIdentifierResolver byName(String name) {
    return registeredResolvers.get(name);
  }


}
