package it.infn.mw.iam.authn.saml.util.metadata;

import java.util.Arrays;

public class SirtfiAttributeMetadataFilter extends AttributeValueMetadataFilter {

  public static final String ASSURANCE_CERTIFICATION_ATTRIBUTE_NAME =
      "urn:oasis:names:tc:SAML:attribute:assurance-certification";

  public static final String SIRTFI_ATTRIBUTE_VALUE = "https://refeds.org/sirtfi";

  public SirtfiAttributeMetadataFilter() {
    super(ASSURANCE_CERTIFICATION_ATTRIBUTE_NAME, Arrays.asList(SIRTFI_ATTRIBUTE_VALUE));
  }

}
