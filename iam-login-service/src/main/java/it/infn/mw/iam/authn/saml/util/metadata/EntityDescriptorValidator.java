package it.infn.mw.iam.authn.saml.util.metadata;

import org.opensaml.saml2.metadata.EntityDescriptor;

public interface EntityDescriptorValidator {

  public ValidationResult validateEntityDescriptor(EntityDescriptor descriptor);
}
