package it.infn.mw.iam.authn.saml;

import java.util.List;

import it.infn.mw.iam.authn.saml.model.IdpDescription;

public interface MetadataLookupService {

  List<IdpDescription> lookupIdp(String text);

  List<IdpDescription> listIdps();
  
  void refreshMetadata();
}
