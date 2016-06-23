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
  
  @Value("${iam.baseUrl}")
  private String baseUrl;

  @Value("${iam.token.lifetime}")
  private Long tokenLifeTime;

  @Bean
  public ConfigurationPropertiesBean config() {

    ConfigurationPropertiesBean config = new ConfigurationPropertiesBean();

    config.setLogoImageUrl("resources/images/indigo-logo.png");
    config.setTopbarTitle("INDIGO IAM server");
    
    if (!issuer.endsWith("/")){
      issuer = issuer + "/";
    }
    
    config.setIssuer(issuer);
    config.setRegTokenLifeTime(tokenLifeTime);
    config.setForceHttps(false);
    config.setLocale(Locale.ENGLISH);

    return config;
  }

}
