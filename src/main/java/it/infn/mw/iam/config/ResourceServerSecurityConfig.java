package it.infn.mw.iam.config;

import org.mitre.oauth2.web.CorsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

public class ResourceServerSecurityConfig extends ResourceServerConfigurerAdapter {

  @Autowired
  private OAuth2AuthenticationEntryPoint authenticationEntryPoint;

  @Autowired
  private CorsFilter corsFilter;

  private void configureApiSecurity(HttpSecurity http) throws Exception {

    // @formatter:off
    http
      .requestMatchers()
        .antMatchers("/api/**")
        .and()
      .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
        .and()
      .sessionManagement()
       .sessionCreationPolicy(SessionCreationPolicy.NEVER);
    // @formatter:on

  }

  private void configureResourceSecurity(HttpSecurity http) throws Exception {

    // @formatter:off
    http
      .requestMatchers()
        .antMatchers("/resource/**")
        .and()
      .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
        .and()
      .addFilterBefore(corsFilter, SecurityContextPersistenceFilter.class)
      .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
      .authorizeRequests()
        .antMatchers("/resource/**")
        .permitAll()
    ;
    // @formatter:on
  }

  private void configureRegisterSecurity(HttpSecurity http) throws Exception {

 // @formatter:off
    http
      .requestMatchers()
        .antMatchers("/register/**")
        .and()
      .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
        .and()
      .addFilterBefore(corsFilter, SecurityContextPersistenceFilter.class)
      .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
      .authorizeRequests()
        .antMatchers("/register/**")
        .permitAll()
    ;
    // @formatter:on
  }

  private void configureUserInfoSecurity(HttpSecurity http) throws Exception {

    // @formatter:off
    http
      .requestMatchers()
        .antMatchers("/userinfo**")
        .and()
      .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
        .and()
      .addFilterBefore(corsFilter, SecurityContextPersistenceFilter.class)
      .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    // @formatter:on

  }

  @Override
  public void configure(HttpSecurity http) throws Exception {

    configureApiSecurity(http);

    configureResourceSecurity(http);

    configureRegisterSecurity(http);

    configureUserInfoSecurity(http);

  }

}
