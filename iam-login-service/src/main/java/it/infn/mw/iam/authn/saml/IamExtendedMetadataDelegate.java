package it.infn.mw.iam.authn.saml;

import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.ExtendedMetadataDelegate;

public class IamExtendedMetadataDelegate extends ExtendedMetadataDelegate {

  public IamExtendedMetadataDelegate(MetadataProvider delegate, ExtendedMetadata md) {
    super(delegate, md);
  }

  @Override
  public boolean isTrustFiltersInitialized() {
    return super.isTrustFiltersInitialized();
  }

  @Override
  public void setTrustFiltersInitialized(boolean trustFiltersInitialized) {
    super.setTrustFiltersInitialized(trustFiltersInitialized);
  }

}
