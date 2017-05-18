package it.infn.mw.iam.authn.saml.util;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.saml.SAMLCredential;

import it.infn.mw.iam.persistence.model.IamSamlId;

public class FirstApplicableChainedSamlIdResolver implements SamlUserIdentifierResolver {

  public static final Logger LOG = LoggerFactory.getLogger(FirstApplicableChainedSamlIdResolver.class);

  private final List<SamlUserIdentifierResolver> resolvers;

  public FirstApplicableChainedSamlIdResolver(List<SamlUserIdentifierResolver> resolvers) {
    this.resolvers = resolvers;
  }

  @Override
  public Optional<IamSamlId> getSamlUserIdentifier(SAMLCredential samlCredential) {

    for (SamlUserIdentifierResolver resolver : resolvers) {
      LOG.debug("Attempting SAML user id resolution with resolver {}",
          resolver.getClass().getName());

      Optional<IamSamlId> userId = resolver.getSamlUserIdentifier(samlCredential);

      if (userId.isPresent()) {

        LOG.debug("Resolved SAML user id: {}", userId);
        return userId;
      }
    }

    LOG.debug(
        "All configured user id resolvers could not resolve the user id from SAML credential");

    return Optional.empty();
  }

}
