package it.infn.mw.iam.config;

import org.mitre.oauth2.service.OAuth2TokenEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;

@Configuration
@Order(9) // This is important! Do not remove
public class ResourceServerConfig {
  
  @Autowired
  private OAuth2AuthenticationEntryPoint authenticationEntryPoint;

  @Autowired
  private OAuth2TokenEntityService tokenService;

  @Bean
  public FilterRegistrationBean disabledAutomaticFilterRegistration(
      final OAuth2AuthenticationProcessingFilter f) {

    FilterRegistrationBean b = new FilterRegistrationBean(f);
    b.setEnabled(false);
    return b;
  }

  @Bean(name = "resourceServerFilter")
  public OAuth2AuthenticationProcessingFilter oauthResourceServerFilter() {

    OAuth2AuthenticationManager manager = new OAuth2AuthenticationManager();
    manager.setTokenServices(tokenService);

    OAuth2AuthenticationProcessingFilter filter = new OAuth2AuthenticationProcessingFilter();
    filter.setAuthenticationEntryPoint(authenticationEntryPoint);
    filter.setAuthenticationManager(manager);
    filter.setStateless(false);
    return filter;
  }
  
}
