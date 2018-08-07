/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2018
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.infn.mw.iam.config.security;

import org.mitre.oauth2.web.CorsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;

@Configuration
@EnableWebSecurity
public class MitreSecurityConfig {

//  @Configuration
//  @Order(9)
//  public static class OAuthResourceServerConfiguration {
//
//    @Autowired
//    private OAuth2AuthenticationEntryPoint authenticationEntryPoint;
//
//    @Autowired
//    private OAuth2TokenEntityService tokenService;
//
//    @Bean
//    public FilterRegistrationBean disabledAutomaticFilterRegistration(
//        final OAuth2AuthenticationProcessingFilter f) {
//
//      FilterRegistrationBean b = new FilterRegistrationBean(f);
//      b.setEnabled(false);
//      return b;
//    }
//
//    @Bean(name = "resourceServerFilter")
//    public OAuth2AuthenticationProcessingFilter oauthResourceServerFilter() {
//
//      OAuth2AuthenticationManager manager = new OAuth2AuthenticationManager();
//      manager.setTokenServices(tokenService);
//
//      OAuth2AuthenticationProcessingFilter filter = new OAuth2AuthenticationProcessingFilter();
//      filter.setAuthenticationEntryPoint(authenticationEntryPoint);
//      filter.setAuthenticationManager(manager);
//      filter.setStateless(false);
//      return filter;
//    }
//
//  }

  @Configuration
  @Order(10)
  public static class MitreApisEndpointAuthorizationConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("resourceServerFilter")
    private OAuth2AuthenticationProcessingFilter resourceFilter;

    @Autowired
    private OAuth2AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private CorsFilter corsFilter;

    @Override
    public void configure(final HttpSecurity http) throws Exception {

      // @formatter:off
      http.antMatcher("/api/**")
          .addFilterAfter(resourceFilter, SecurityContextPersistenceFilter.class)
          .addFilterBefore(corsFilter, WebAsyncManagerIntegrationFilter.class)
          .exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint)
          .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.NEVER)
          .and()
            .csrf().disable();
      // @formatter:on

    }
  }

  @Configuration
  @Order(11)
  public static class ResourceEndpointAuthorizationConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuth2AuthenticationProcessingFilter resourceFilter;

    @Autowired
    private OAuth2AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private CorsFilter corsFilter;

    @Override

    public void configure(final HttpSecurity http) throws Exception {

      // @formatter:off
      http.antMatcher("/resource/**")
        .exceptionHandling()
          .authenticationEntryPoint(authenticationEntryPoint).and()
        .addFilterAfter(resourceFilter, SecurityContextPersistenceFilter.class)
        .addFilterBefore(corsFilter, WebAsyncManagerIntegrationFilter.class)
        .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
          .authorizeRequests()
            .antMatchers("/resource/**").permitAll()
        .and()
          .csrf().disable();
      // @formatter:on
    }
  }

  @Configuration
  @Order(12)
  public static class RegisterEndpointAuthorizationConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuth2AuthenticationProcessingFilter resourceFilter;

    @Autowired
    private OAuth2AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private CorsFilter corsFilter;

    @Override
    public void configure(final HttpSecurity http) throws Exception {

      // @formatter:off
      http.antMatcher("/register/**")
        .exceptionHandling()
          .authenticationEntryPoint(authenticationEntryPoint)
        .and()
          .addFilterAfter(resourceFilter, SecurityContextPersistenceFilter.class)
          .addFilterBefore(corsFilter, WebAsyncManagerIntegrationFilter.class)
        .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
          .authorizeRequests()
            .antMatchers("/register/**").permitAll()
          .and()
            .csrf().disable();
      // @formatter:on
    }
  }

  @Configuration
  @Order(13)
  public static class UserInfoEndpointAuthorizationConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuth2AuthenticationProcessingFilter resourceFilter;

    @Autowired
    private OAuth2AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private CorsFilter corsFilter;

    @Override
    public void configure(final HttpSecurity http) throws Exception {

      // @formatter:off
      http.antMatcher("/userinfo**")
        .exceptionHandling()
          .authenticationEntryPoint(authenticationEntryPoint)
        .and()
          .addFilterAfter(resourceFilter, SecurityContextPersistenceFilter.class)
          .addFilterBefore(corsFilter, WebAsyncManagerIntegrationFilter.class)
        .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
          .csrf().disable();
      // @formatter:on
    }
  }

  @Configuration
  @Order(15)
  public static class IntrospectEndpointAuthorizationConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuth2AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    @Qualifier("clientUserDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    private CorsFilter corsFilter;

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {

      auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

      // @formatter:off
      http.antMatcher("/introspect/**")
        .httpBasic()
          .authenticationEntryPoint(authenticationEntryPoint)
        .and()
          .addFilterBefore(corsFilter, SecurityContextPersistenceFilter.class)
            .exceptionHandling()
          .authenticationEntryPoint(authenticationEntryPoint)
        .and()
          .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
        .csrf()
          .disable()
        .authorizeRequests()
          .anyRequest()
            .fullyAuthenticated();
      // @formatter:on
    }
  }

  @Configuration
  @Order(16)
  public static class RevokeEndpointAuthorizationConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuth2AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    @Qualifier("clientUserDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    private CorsFilter corsFilter;

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {

      auth.userDetailsService(userDetailsService);
    }

    private ClientCredentialsTokenEndpointFilter clientCredentialsEndpointFilter()
        throws Exception {

      ClientCredentialsTokenEndpointFilter filter =
          new ClientCredentialsTokenEndpointFilter("/revoke");
      filter.setAuthenticationManager(authenticationManager());
      return filter;
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

      // @formatter:off
      http.antMatcher("/revoke**")
        .httpBasic()
          .authenticationEntryPoint(authenticationEntryPoint)
        .and()
          .addFilterBefore(corsFilter, SecurityContextPersistenceFilter.class)
          .addFilterBefore(clientCredentialsEndpointFilter(), BasicAuthenticationFilter.class)
        .exceptionHandling()
          .authenticationEntryPoint(authenticationEntryPoint)
        .and()
          .csrf().disable()
          .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
      // @formatter:on
    }
  }

  @Configuration
  @Order(17)
  public static class JwkEndpointAuthorizationConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private Http403ForbiddenEntryPoint http403ForbiddenEntryPoint;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

      // @formatter:off
      http.antMatcher("/jwk**")
        .exceptionHandling()
          .authenticationEntryPoint(http403ForbiddenEntryPoint)
        .and()
          .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
          .authorizeRequests()
            .antMatchers("/**").permitAll();
      // @formatter:on
    }
  }

  @Configuration
  @Order(27)
  public static class DeviceCodeEnpointConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("clientUserDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    private CorsFilter corsFilter;

    @Autowired
    private OAuth2AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {

      auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

      // @formatter:off
      http.antMatcher("/devicecode/**")
        .httpBasic()
          .authenticationEntryPoint(authenticationEntryPoint)
        .and()
          .addFilterBefore(corsFilter, SecurityContextPersistenceFilter.class)
          .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
        .csrf()
          .disable()
        .authorizeRequests()
          .anyRequest()
            .fullyAuthenticated();
      // @formatter:on
    }
  }

  @Configuration
  @Order(Ordered.HIGHEST_PRECEDENCE)
  @Profile("dev")
  public static class H2ConsoleEndpointAuthorizationConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

      HttpSecurity h2Console = http.requestMatchers()
        .antMatchers("/h2-console", "/h2-console/**")
        .and()
        .csrf()
        .disable();

      h2Console.httpBasic();
      h2Console.headers().frameOptions().disable();

      h2Console.authorizeRequests().antMatchers("/h2-console/**", "/h2-console").permitAll();
    }

    @Override
    public void configure(final WebSecurity builder) throws Exception {
      builder.debug(true);
    }
  }
}
