package it.infn.mw.iam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.h2.H2ConsoleAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@ComponentScan(
    basePackages = {"it.infn.mw.iam.config", "it.infn.mw.iam.saml", "it.infn.mw.iam.persistence",
        "it.infn.mw.iam.oidc", "it.infn.mw.iam.core.web", "it.infn.mw.iam.api",
        "org.mitre.oauth2.web", "org.mitre.oauth2.view", "org.mitre.openid.connect.web",
        "org.mitre.openid.connect.view", "org.mitre.discovery.web", "org.mitre.discovery.view"})

@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class, OAuth2AutoConfiguration.class,
    H2ConsoleAutoConfiguration.class})
public class IamLoginService {

  public static void main(final String[] args) {

    SpringApplication.run(IamLoginService.class, args);
  }
}
