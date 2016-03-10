package it.infn.mw.iam.config;

import org.mitre.oauth2.web.CorsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired
  private CorsFilter corsFilter;

  @Autowired
  private Http403ForbiddenEntryPoint http403ForbiddenEntryPoint;

  @Autowired
  private OAuth2AuthenticationEntryPoint authenticationEntryPoint;

  @Autowired
  @Qualifier("clientUserDetailsService")
  private UserDetailsService userDetailsService;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {

    auth.userDetailsService(userDetailsService);
  }

  public ClientCredentialsTokenEndpointFilter clientCredentialsIntrospectionEndpointFilter()
    throws Exception {

    ClientCredentialsTokenEndpointFilter filter = new ClientCredentialsTokenEndpointFilter(
      "/introspect");
    filter.setAuthenticationManager(authenticationManager());
    return filter;
  }

  public ClientCredentialsTokenEndpointFilter clientCredentialsRevokeEndpointFilter()
    throws Exception {

    ClientCredentialsTokenEndpointFilter filter = new ClientCredentialsTokenEndpointFilter(
      "/revoke");
    filter.setAuthenticationManager(authenticationManager());
    return filter;
  }

  public ClientCredentialsTokenEndpointFilter clientCredentialsTokenEndpointFilter()
    throws Exception {

    ClientCredentialsTokenEndpointFilter filter = new ClientCredentialsTokenEndpointFilter(
      "/token");
    filter.setAuthenticationManager(authenticationManager());
    return filter;
  }

  private void configureJwkEndpointSecurity(HttpSecurity http)
    throws Exception {

    // @formatter:off
    http
      .requestMatchers()
        .antMatchers("/jwk**")
        .and()
      .exceptionHandling()
        .authenticationEntryPoint(http403ForbiddenEntryPoint)
        .and()
      .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
    .authorizeRequests()
      .antMatchers("/jwk**")
      .permitAll()
    ;
    // @formatter:on
  }

  private void configureIntrospectSecurity(HttpSecurity http) throws Exception {

    // @formatter:off
    http
      .requestMatchers()
        .antMatchers("/introspect**")
        .and()
      .httpBasic()
        .authenticationEntryPoint(authenticationEntryPoint)
        .and()
      .addFilterBefore(corsFilter, SecurityContextPersistenceFilter.class)
      .addFilterBefore(clientCredentialsIntrospectionEndpointFilter(), BasicAuthenticationFilter.class) 
      .exceptionHandling()
        .authenticationEntryPoint(authenticationEntryPoint)
        .and()
      .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    ;
    // @formatter:on
  }

  private void configureRevokeSecurity(HttpSecurity http) throws Exception {

    // @formatter:off
    http
      .requestMatchers()
        .antMatchers("/revoke**")
        .and()
      .httpBasic()
        .authenticationEntryPoint(authenticationEntryPoint)
        .and()
      .addFilterBefore(corsFilter, SecurityContextPersistenceFilter.class)
      .addFilterBefore(clientCredentialsRevokeEndpointFilter(), BasicAuthenticationFilter.class) 
      .exceptionHandling()
        .authenticationEntryPoint(authenticationEntryPoint)
        .and()
      .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    ;
    // @formatter:on
  }

  private void configureTokenEndpointSecurity(HttpSecurity http)
    throws Exception {

    // @formatter:off
    http
    .requestMatchers()
      .antMatchers("/token")
      .and()
    .httpBasic()
      .authenticationEntryPoint(authenticationEntryPoint)
      .and()
    .authorizeRequests()
      .antMatchers(HttpMethod.OPTIONS, "/token").permitAll()
      .antMatchers("/token").authenticated()
      .and()
    .addFilterBefore(clientCredentialsIntrospectionEndpointFilter(), BasicAuthenticationFilter.class) 
    .addFilterBefore(corsFilter, SecurityContextPersistenceFilter.class)
    .exceptionHandling()
      .authenticationEntryPoint(authenticationEntryPoint)
      .accessDeniedHandler(new OAuth2AccessDeniedHandler())
      .and()
    .sessionManagement()
      .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    // @formatter:on

  }

  private void configureStaticResourcesSecurity(HttpSecurity http)
    throws Exception {

    // @formatter:off
    http
      .requestMatchers()
        .antMatchers("/resources/**")
        .and()
      .authorizeRequests()
        .antMatchers("/resources/**").permitAll()
        .and()
      .addFilterBefore(corsFilter, SecurityContextPersistenceFilter.class)
      .exceptionHandling()
        .authenticationEntryPoint(http403ForbiddenEntryPoint)
        .and()
      .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    ;
    // @formatter:on
  }




  private void configureH2ConsoleEndpointSecurity(HttpSecurity http)
    throws Exception {

    // @formatter:off
    http
    .authorizeRequests()
    .antMatchers("/h2-console/**").permitAll();
    // @formatter:on
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    configureStaticResourcesSecurity(http);

    configureTokenEndpointSecurity(http);

    configureIntrospectSecurity(http);

    configureJwkEndpointSecurity(http);

    configureRevokeSecurity(http);
    
    configureH2ConsoleEndpointSecurity(http);

  }
}
