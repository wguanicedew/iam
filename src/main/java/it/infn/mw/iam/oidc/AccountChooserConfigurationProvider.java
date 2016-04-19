package it.infn.mw.iam.oidc;

import java.util.Collection;
import java.util.List;

public interface AccountChooserConfigurationProvider {

  public List<String> clientRedirectURIs();

  public Collection<OidcProviderTO> providers();

}
