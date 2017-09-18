package it.infn.mw.iam.authn.saml.util.metadata;

import static java.util.Arrays.asList;

public class ResearchAndScholarshipMetadataFilter extends AttributeValueMetadataFilter {

  public static final String ENTITY_CATEGORY_ATTRIBUTE_NAME =
      "http://macedir.org/entity-category-support";

  public static final String R_S_ATTRIBUTE_VALUE =
      "http://refeds.org/category/research-and-scholarship";

  public ResearchAndScholarshipMetadataFilter() {
    super(ENTITY_CATEGORY_ATTRIBUTE_NAME, asList(R_S_ATTRIBUTE_VALUE));

  }

}
