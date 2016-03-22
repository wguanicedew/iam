package it.infn.mw.iam.config;

import java.util.Locale;

import org.mitre.openid.connect.config.ConfigurationPropertiesBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MitreConfig {

  @Value("${iam.issuer}")
  private String issuer;

  @Value("${iam.token.lifetime}")
  private Long tokenLifeTime;

  @Bean
  public ConfigurationPropertiesBean config() {

    ConfigurationPropertiesBean config = new ConfigurationPropertiesBean();

    config.setLogoImageUrl("resources/images/openid_connect_small.png");
    config.setTopbarTitle("INDIGO IAM server");

    config.setIssuer(issuer);

    config.setRegTokenLifeTime(tokenLifeTime);

    config.setForceHttps(false);

    config.setLocale(Locale.ENGLISH);

    return config;
  }

}
