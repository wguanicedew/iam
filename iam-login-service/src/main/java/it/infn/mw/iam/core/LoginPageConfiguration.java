package it.infn.mw.iam.core;

public interface LoginPageConfiguration {

  boolean isGoogleEnabled();

  boolean isGithubEnabled();

  boolean isSamlEnabled();

  boolean isRegistrationEnabled();
}
