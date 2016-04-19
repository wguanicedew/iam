package it.infn.mw.iam.oidc;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class IamAccountChooserConfigurationProvider
  implements AccountChooserConfigurationProvider {

  private final List<String> redirectURIs;
  private final List<OidcProviderTO> providers;

  public IamAccountChooserConfigurationProvider(String iamBaseUrl) {

    String googleURL = String.format("%s/openid_connect_login", iamBaseUrl);
    String ghURL = String.format("%s/gh_login", iamBaseUrl);
    redirectURIs = Arrays.asList(googleURL, ghURL);

    OidcProviderTO google = new OidcProviderTO();
    google.setClientUri(googleURL);
    google.setCssClass("google");
    google.setDescriptor("Google");
    google.setIssuer("https://accounts.google.com/");

    OidcProviderTO github = new OidcProviderTO();
    github.setClientUri(ghURL);
    github.setCssClass("github");
    github.setDescriptor("Github");
    github.setClientUri("https://github.com/");

    providers = Arrays.asList(google, github);
  }

  @Override
  public List<String> clientRedirectURIs() {

    return redirectURIs;
  }

  @Override
  public Collection<OidcProviderTO> providers() {

    return providers;
  }

}
