package it.infn.mw.iam.core;

import it.infn.mw.iam.config.oidc.GoogleClientProperties;

public interface LoginPageConfiguration {

  public boolean isGoogleEnabled();

  public boolean isGithubEnabled();

  public boolean isSamlEnabled();

  public GoogleClientProperties getGoogleConfiguration();

}
