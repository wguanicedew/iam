package it.infn.mw.iam.core.web;

import java.util.Optional;

public interface LoginPageConfiguration {

  boolean isGoogleEnabled();

  boolean isGithubEnabled();

  boolean isSamlEnabled();

  boolean isRegistrationEnabled();
  
  boolean isAccountLinkingEnabled();
  
  Optional<String> getPrivacyPolicyUrl();
  
  String getPrivacyPolicyText();
  
  String getLoginButtonText();
  
}
