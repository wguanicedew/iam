package it.infn.mw.iam.core;

import it.infn.mw.iam.config.oidc.GoogleClientConfig;

public interface LoginPageConfiguration {

  public boolean isGoogleEnabled();

  public boolean isGithubEnabled();

  public boolean isSamlEnabled();

  public GoogleClientConfig getGoogleConfiguration();

}
