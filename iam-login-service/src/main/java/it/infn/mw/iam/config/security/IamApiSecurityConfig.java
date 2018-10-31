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

import static it.infn.mw.iam.api.tokens.Constants.ACCESS_TOKENS_ENDPOINT;
import static it.infn.mw.iam.api.tokens.Constants.REFRESH_TOKENS_ENDPOINT;

import org.mitre.oauth2.web.CorsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;

import it.infn.mw.iam.config.CustomAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class IamApiSecurityConfig {

  @Configuration
  @Order(20)
  public static class IamApiConfig extends WebSecurityConfigurerAdapter {

    private static final String AUP_PATH = "/iam/aup";
    private static final String GROUP_REQUEST_PATH = "/iam/group_requests";

    @Autowired
    private OAuth2AuthenticationProcessingFilter resourceFilter;

    @Autowired
    private OAuth2AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private CorsFilter corsFilter;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
      // @formatter:off
      http.requestMatchers()
        .antMatchers("/scim/**", 
            "/registration/**", 
            "/iam/password-update", 
            "/iam/scope_policies/**",
            ACCESS_TOKENS_ENDPOINT + "/**", REFRESH_TOKENS_ENDPOINT + "/**",
            "/iam/aup/**",
            GROUP_REQUEST_PATH+"**", GROUP_REQUEST_PATH+"/**",
            "/iam/account/search**", 
            "/iam/group/search**")
        .and()
          .exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint)
        .and()
          .addFilterAfter(resourceFilter, SecurityContextPersistenceFilter.class)
          .addFilterBefore(corsFilter, WebAsyncManagerIntegrationFilter.class)
        .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.NEVER)
        .and()
          .authorizeRequests()
            .antMatchers("/scim/**").authenticated()
            .antMatchers(HttpMethod.POST, "/registration/create").permitAll()
            .antMatchers(HttpMethod.GET, "/registration/username-available/**").permitAll()
            .antMatchers(HttpMethod.GET, "/registration/email-available/**").permitAll()
            .antMatchers(HttpMethod.GET, "/registration/confirm/**").permitAll()
            .antMatchers(HttpMethod.GET, "/registration/verify/**").permitAll()
            .antMatchers(HttpMethod.GET, "/registration/submitted").permitAll()
            .antMatchers("/registration/**").authenticated()
            .antMatchers("/iam/password-update").authenticated()
            .antMatchers("/iam/scope_policies/**").authenticated()
            .antMatchers(ACCESS_TOKENS_ENDPOINT + "/**", REFRESH_TOKENS_ENDPOINT + "/**").authenticated()
            .antMatchers(HttpMethod.GET, AUP_PATH).permitAll()
            .antMatchers(HttpMethod.POST, AUP_PATH).authenticated()
            .antMatchers(HttpMethod.DELETE, AUP_PATH).authenticated()
            .antMatchers(GROUP_REQUEST_PATH+"**", GROUP_REQUEST_PATH+"/**" ).authenticated()
            .antMatchers(HttpMethod.GET, "/iam/account/search", "/iam/group/search").permitAll()
            .antMatchers(HttpMethod.GET, "/iam/config/**").permitAll()
        .and()
          .csrf().disable();
      // @formatter:on
    }
  }

  @Configuration
  @Order(21)
  public static class AuthnInfoEndpointConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuth2AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      //@formatter:off
      http.antMatcher("/iam/authn-info/**")
        .exceptionHandling()
          .authenticationEntryPoint(authenticationEntryPoint)
        .and()
          .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.NEVER)
        .and()
          .authorizeRequests()
            .antMatchers("/iam/authn-info/**").authenticated();
      //@formatter:on
    }
  }

  @Configuration
  @Order(22)
  public static class AccountLinkingEndpointConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuth2AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      //@formatter:off
      http.antMatcher("/iam/account-linking/**")
        .exceptionHandling()
          .authenticationEntryPoint(authenticationEntryPoint)
        .and()
          .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.NEVER)
        .and()
          .csrf().disable()
        .authorizeRequests()
          .antMatchers("/iam/account-linking/**").authenticated();
      //@formatter:on
    }
  }

  @Configuration
  @Order(23)
  public static class AccountAuthorityEndpointConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuth2AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      //@formatter:off
      http.requestMatchers()
        .antMatchers("/iam/account/**", "/iam/me/**")
        .and()
          .exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint)
        .and()
          .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.NEVER)
        .and()
          .authorizeRequests()
            .antMatchers("/iam/account/**", "/iam/me/**").authenticated()
        .and()
          .csrf().disable();
      //@formatter:on
    }
  }

  @Configuration
  @Order(24)
  public static class ActuatorEndpointsConfig extends WebSecurityConfigurerAdapter {

    @Value("${iam.superuser.username}")
    private String basicUsername;

    @Value("${iam.superuser.password}")
    private String basicPassword;

    @Autowired
    private OAuth2AuthenticationProcessingFilter resourceFilter;

    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntyPoint;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
      // @formatter:off
      auth.inMemoryAuthentication()
        .withUser(basicUsername).password(basicPassword)
        .roles("SUPERUSER", "ADMIN");
      // @formatter:on
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
      // @formatter:off
      http
        .requestMatchers()
          .antMatchers("/metrics", "/info", "/health", "/health/mail", "/health/external",
              "/configprops", "/env", "/mappings", "/flyway", "/autoconfig", "/beans", "/dump", "/trace")
        .and()
          .httpBasic()
          .authenticationEntryPoint(customAuthenticationEntyPoint)
        .and()
          .exceptionHandling()
            .accessDeniedHandler(new AccessDeniedHandlerImpl())
        .and()
          .addFilterAfter(resourceFilter, SecurityContextPersistenceFilter.class)
        .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.NEVER)
        .and()
          .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/info", "/health", "/health/mail", "/health/external").permitAll()
            .antMatchers(HttpMethod.GET, "/metrics").hasRole("ADMIN")
            .antMatchers(HttpMethod.GET, "/configprops", "/env", "/mappings", "/flyway", "/autoconfig", "/beans", "/dump", "/trace").hasRole("SUPERUSER");
      // @formatter:on
    }
  }
}
