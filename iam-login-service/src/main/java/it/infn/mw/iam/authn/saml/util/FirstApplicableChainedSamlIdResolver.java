package it.infn.mw.iam.authn.saml.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.saml.SAMLCredential;

public class FirstApplicableChainedSamlIdResolver implements SamlUserIdentifierResolver {

  public static final Logger LOG = LoggerFactory.getLogger(FirstApplicableChainedSamlIdResolver.class);

  private final List<SamlUserIdentifierResolver> resolvers;

  public FirstApplicableChainedSamlIdResolver(List<SamlUserIdentifierResolver> resolvers) {
    this.resolvers = resolvers;
  }

  @Override
  public SamlUserIdentifierResolutionResult resolveSamlUserIdentifier(SAMLCredential samlCredential) {

    List<String> errorMessages = new ArrayList<>();
    
    for (SamlUserIdentifierResolver resolver : resolvers) {
      LOG.debug("Attempting SAML user id resolution with resolver {}",
          resolver.getClass().getName());

      SamlUserIdentifierResolutionResult result = resolver
          .resolveSamlUserIdentifier(samlCredential);
      
      if (result.getResolvedId().isPresent()){
        LOG.debug("Resolved SAML user id: {}", result.getResolvedId().get());
        return result;
      }
      
      result.getErrorMessages().ifPresent(m -> errorMessages.addAll(m));
    }

    LOG.debug(
        "All configured user id resolvers could not resolve the user id from SAML credential");

    return SamlUserIdentifierResolutionResult.resolutionFailure(errorMessages);
  }

}
