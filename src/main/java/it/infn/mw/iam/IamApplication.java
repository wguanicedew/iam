package it.infn.mw.iam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@ComponentScan(basePackages = { "it.infn.mw.iam.config",
  "it.infn.mw.iam.client", "it.infn.mw.iam.saml", "it.infn.mw.iam.persistence",
  "org.mitre.oauth2.web", "org.mitre.oauth2.view",
  "org.mitre.openid.connect.web", "org.mitre.openid.connect.view",
  "org.mitre.discovery.web", "org.mitre.discovery.view" })

@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class })
public class IamApplication {

  public static void main(final String[] args) {

    SpringApplication.run(IamApplication.class, args);
  }
}
