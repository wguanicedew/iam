package it.infn.mw.iam.authn.saml.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.saml.metadata.MetadataManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.infn.mw.iam.authn.saml.MetadataLookupService;

@RestController
@Profile("saml")
public class SamlRefreshMetadataController {
  
  @Autowired
  MetadataManager metadataManager;
  
  @Autowired
  MetadataLookupService metadataLookupService;

  @PreAuthorize("hasRole('ADMIN')")
  @RequestMapping(value="/saml/refresh-metadata")
  public String refreshMetadata() {
    metadataManager.setRefreshRequired(true);
    metadataManager.refreshMetadata();
    metadataLookupService.refreshMetadata();
    return "ok";
  }

}
