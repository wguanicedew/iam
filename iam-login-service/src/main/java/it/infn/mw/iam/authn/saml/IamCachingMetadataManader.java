package it.infn.mw.iam.authn.saml;

import java.util.ArrayList;
import java.util.List;

import org.opensaml.saml2.metadata.provider.MetadataFilter;
import org.opensaml.saml2.metadata.provider.MetadataFilterChain;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.saml2.metadata.provider.SignatureValidationFilter;
import org.opensaml.xml.signature.SignatureTrustEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.saml.metadata.CachingMetadataManager;
import org.springframework.security.saml.metadata.ExtendedMetadataDelegate;

public class IamCachingMetadataManader extends CachingMetadataManager {

  public static final Logger LOGGER = LoggerFactory.getLogger(IamCachingMetadataManader.class);

  public IamCachingMetadataManader(List<MetadataProvider> providers)
      throws MetadataProviderException {
    super(providers);
  }

  @Override
  protected void initializeProviderFilters(ExtendedMetadataDelegate p)
      throws MetadataProviderException {

    if (!(p instanceof IamExtendedMetadataDelegate)){
      super.initializeProviderFilters(p);
      return;
    }
    
    IamExtendedMetadataDelegate provider = (IamExtendedMetadataDelegate) p;

    if (provider.isTrustFiltersInitialized()) {
      LOGGER.debug(
          "Metadata provider was already initialized, "
          + "signature filter initialization will be skipped");

    } else {

      boolean requireSignature = provider.isMetadataRequireSignature();
      SignatureTrustEngine trustEngine = getTrustEngine(provider);
      SignatureValidationFilter signatureFilter = new SignatureValidationFilter(trustEngine);
      signatureFilter.setRequireSignature(requireSignature);
      
      LOGGER.debug("Created new trust manager for metadata provider {}", provider);
      
      MetadataFilter currentFilter = provider.getMetadataFilter();
      
      List<MetadataFilter> filters = new ArrayList<>();
      
      // Signature checks first!
      filters.add(signatureFilter);
      
      if (currentFilter != null){
        filters.add(currentFilter);
      }

      MetadataFilterChain chain = new MetadataFilterChain();
      chain.setFilters(filters);
      
      provider.setMetadataFilter(chain);
      
      provider.setTrustFiltersInitialized(true);
    }

  }

}
