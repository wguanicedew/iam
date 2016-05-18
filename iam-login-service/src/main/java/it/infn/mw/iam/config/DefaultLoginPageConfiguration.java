package it.infn.mw.iam.config;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import it.infn.mw.iam.config.oidc.GoogleClientConfig;
import it.infn.mw.iam.core.LoginPageConfiguration;

@Component
public class DefaultLoginPageConfiguration
  implements LoginPageConfiguration, EnvironmentAware {

  private Environment env;

  private boolean googleEnabled;
  private boolean githubEnabled;
  private boolean samlEnabled;

  @Autowired
  GoogleClientConfig googleClientConfiguration;

  @PostConstruct
  public void init() {

    googleEnabled = activeProfilesContains("google");
    githubEnabled = activeProfilesContains("github");
    samlEnabled = activeProfilesContains("saml");
  }

  private boolean activeProfilesContains(String val) {

    return Arrays.asList(env.getActiveProfiles())
      .contains(val);
  }

  @Override
  public boolean isGoogleEnabled() {

    return googleEnabled;
  }

  @Override
  public boolean isGithubEnabled() {

    return githubEnabled;
  }

  @Override
  public boolean isSamlEnabled() {

    return samlEnabled;
  }

  @Override
  public void setEnvironment(Environment environment) {

    this.env = environment;

  }

  @Override
  public GoogleClientConfig getGoogleConfiguration() {

    return googleClientConfiguration;
  }

}
