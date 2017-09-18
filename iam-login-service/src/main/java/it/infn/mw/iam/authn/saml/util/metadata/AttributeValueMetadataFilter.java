package it.infn.mw.iam.authn.saml.util.metadata;

import static it.infn.mw.iam.authn.saml.util.metadata.ValidationResult.invalid;
import static it.infn.mw.iam.authn.saml.util.metadata.ValidationResult.valid;
import static java.lang.String.format;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.samlext.saml2mdattr.EntityAttributes;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.XSAny;
import org.opensaml.xml.schema.XSString;

public class AttributeValueMetadataFilter extends AbstractMetadataFilter {
  
  private final String attributeName;
  private final String attributeNameFormat;
  private final List<String> requiredValues;

  public AttributeValueMetadataFilter(String attributeName, String attributeNameFormat,
      List<String> requiredValues) {
    this.attributeName = attributeName;
    this.attributeNameFormat = attributeNameFormat;
    this.requiredValues = requiredValues;
  }

  public AttributeValueMetadataFilter(String attributeName, List<String> requiredValues) {
    this(attributeName, Attribute.URI_REFERENCE, requiredValues);
  }

  private String join(List<String> list) {
    return list.stream().collect(Collectors.joining(","));
  }

  private String getXsStringAttributeValue(XSString string) {
    return string.getValue();
  }

  private String getXsAnyAttributeValue(XSAny any) {
    return any.getTextContent();
  }

  // This function explains why people hate opensaml
  private String getAttributeValue(XMLObject object) {
    if (object instanceof XSString) {
      return getXsStringAttributeValue((XSString) object);
    }
    if (object instanceof XSAny) {
      return getXsAnyAttributeValue((XSAny) object);
    }
    return null;
  }

  @Override
  protected ValidationResult validateEntityDescriptor(EntityDescriptor descriptor) {
    if (descriptor.getExtensions() == null) {
      return invalid(format("No extensions present for descriptor '%s'", descriptor.getEntityID()));
    }

    for (final XMLObject object : descriptor.getExtensions()
      .getUnknownXMLObjects(EntityAttributes.DEFAULT_ELEMENT_NAME)) {

      EntityAttributes attributes = (EntityAttributes) object;

      for (Attribute attr : attributes.getAttributes()) {

        if (attributeName.equals(attr.getName())
            && attributeNameFormat.equals(attr.getNameFormat())) {
          
          List<String> attributeValues = attr.getAttributeValues()
            .stream()
            .map(this::getAttributeValue)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

          if (attributeValues.containsAll(requiredValues)) {
            return valid();
          } else {
            return invalid(format("Attribute '%s' has values '%s'. Required values: '%s'",
                attributeName, join(attributeValues), join(requiredValues)));
          }
        }
      }
    }

    return invalid(String.format(
        "Attribute '%s' with required values '%s' not found in EntityAttributes for entity '%s'",
        attributeName, join(requiredValues), descriptor.getEntityID()));
  }

}
