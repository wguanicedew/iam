package it.infn.mw.iam.config;

import java.util.Locale;
import java.util.Set;

import org.mitre.openid.connect.config.ConfigurationPropertiesBean;
import org.mitre.openid.connect.config.UIConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Sets;

@Configuration
public class MitreConfig {

  @Value("${iam.issuer}")
  private String issuer;

  @Value("${iam.baseUrl}")
  private String baseUrl;

  @Value("${iam.token.lifetime}")
  private Long tokenLifeTime;

  @Value("${iam.logoImageUrl}")
  private String logoImageUrl;

  @Value("${iam.topbarTitle}")
  private String topbarTitle;

  @Bean
  public ConfigurationPropertiesBean config() {

    ConfigurationPropertiesBean config = new ConfigurationPropertiesBean();

    config.setLogoImageUrl(logoImageUrl);
    config.setTopbarTitle(topbarTitle);

    if (!issuer.endsWith("/")) {
      issuer = issuer + "/";
    }

    config.setIssuer(issuer);
    
    if (tokenLifeTime <= 0L){
      config.setRegTokenLifeTime(null);
    } else {
      config.setRegTokenLifeTime(tokenLifeTime);
    }
    
    config.setForceHttps(false);
    config.setLocale(Locale.ENGLISH);

    return config;
  }


  @Bean
  public UIConfiguration uiConfiguration() {
    
    Set<String> jsFiles = Sets.newHashSet("resources/js/client.js",
        "resources/js/grant.js",
        "resources/js/scope.js",
        "resources/js/whitelist.js",
        "resources/js/dynreg.js",
        "resources/js/rsreg.js",
        "resources/js/token.js",
        "resources/js/blacklist.js",
        "resources/js/profile.js");
    
    UIConfiguration config = new UIConfiguration();
    config.setJsFiles(jsFiles);
    return config;

  }
}
