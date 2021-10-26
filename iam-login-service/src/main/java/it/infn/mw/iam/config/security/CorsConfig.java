package it.infn.mw.iam.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

  private static final String[] CORS_ENDPOINT_MATCHERS =
  // @formatter:off
    {
        "/api/**", 
        "/resource/**", 
        "/register/**",
        "/iam/**", 
        "/scim/**", 
        "/token", 
        "/introspect", 
        "/userinfo", 
        "/revoke/**", 
        "/jwk",
        "/devicode"
    };
    //@formatter:on

  @Bean
  CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration corsConfig = new CorsConfiguration();
    corsConfig.applyPermitDefaultValues();

    for (String m : CORS_ENDPOINT_MATCHERS) {
      source.registerCorsConfiguration(m, corsConfig);
    }

    return new CorsFilter(source);
  }
}
