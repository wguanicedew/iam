package it.infn.mw.iam.authn.saml.util.metadata;

import static it.infn.mw.iam.authn.saml.util.metadata.ValidationResult.invalid;
import static it.infn.mw.iam.authn.saml.util.metadata.ValidationResult.valid;

import java.util.Collections;
import java.util.List;

import org.opensaml.saml2.metadata.EntityDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityIdWhitelistMetadataFilter extends AbstractMetadataFilter {

  public static final Logger LOG = LoggerFactory.getLogger(EntityIdWhitelistMetadataFilter.class);

  private final List<String> entityIdWhitelist;

  public EntityIdWhitelistMetadataFilter(List<String> entityIdWhitelist) {
    this.entityIdWhitelist = Collections.unmodifiableList(entityIdWhitelist);
  }

  protected boolean entityDescriptorIsWhitelisted(EntityDescriptor descriptor) {
    if (!entityIdWhitelist.contains(descriptor.getEntityID())) {
      LOG.debug("Entity id '{}' not found in whitelist, entity metadata will be ignored",
          descriptor.getEntityID());
      return false;
    }

    return true;
  }

  @Override
  protected ValidationResult validateEntityDescriptor(EntityDescriptor descriptor) {

    if (!entityIdWhitelist.contains(descriptor.getEntityID())) {
      final String msg =
          String.format("Entity id '%s' not found in whitelist, entity metadata will be ignored",
              descriptor.getEntityID());
      return invalid(msg);
    }
    
    return valid();
  }



}
