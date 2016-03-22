package it.infn.mw.iam.config;

import javax.sql.DataSource;

import org.mitre.oauth2.web.CorsFilter;
import org.mitre.openid.connect.client.OIDCAuthenticationProvider;
import org.mitre.openid.connect.web.AuthenticationTimeStamper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import it.infn.mw.iam.libs.IndigoOIDCAuthFilter;

@Configuration
@EnableWebSecurity
@EnableResourceServer
public class SecurityConfig {

  @Configuration
  @Order(1)
  public static class UserLoginConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private AuthenticationTimeStamper authenticationTimeStamper;

    @Autowired
    private OAuth2WebSecurityExpressionHandler oAuth2WebSecurityExpressionHandler;

    @Autowired
    @Qualifier("mitreAuthzRequestFilter")
    private GenericFilterBean authorizationRequestFilter;

    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth)
      throws Exception {

      auth.jdbcAuthentication().dataSource(dataSource);
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {

      web.expressionHandler(oAuth2WebSecurityExpressionHandler);
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

      // @formatter:off
      
      http
        .sessionManagement()
          .enableSessionUrlRewriting(false)
        .and()
          .authorizeRequests()
            .antMatchers("/login**").permitAll()
        .and()
          .formLogin()
            .loginPage("/login")
            .failureUrl("/login?error=failure")
            .successHandler(authenticationTimeStamper)
        .and()
          .addFilterBefore(authorizationRequestFilter, SecurityContextPersistenceFilter.class)
        .logout()
          .logoutUrl("/logout")
          .and()
        .anonymous()
        .and()
        .csrf()
          .requireCsrfProtectionMatcher(new AntPathRequestMatcher("/authorize"))
        .disable();
      ;
      // @formatter:on

    }

    @Override
    @Bean(name = "authenticationManager")
    public AuthenticationManager authenticationManagerBean() throws Exception {

      return super.authenticationManagerBean();
    }

    @Bean
    public OAuth2WebSecurityExpressionHandler oAuth2WebSecurityExpressionHandler() {

      return new OAuth2WebSecurityExpressionHandler();
    }
  }

  @Configuration
  @Order(5)
  public static class ExternalLogin extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("OIDCAuthenticationManager")
    private AuthenticationManager oidcAuthManager;

    @Autowired
    OIDCAuthenticationProvider authProvider;

    @Autowired
    @Qualifier("openIdConnectAuthenticationFilter")
    private IndigoOIDCAuthFilter oidcFilter;

    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {

      return oidcAuthManager;
    }

    @Bean(name = "ExternalAuthenticationEntryPoint")
    public LoginUrlAuthenticationEntryPoint authenticationEntryPoint() {

      return new LoginUrlAuthenticationEntryPoint("/openid_connect_login");
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth)
      throws Exception {

      auth.authenticationProvider(authProvider);
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

      //@formatter:off
      http
        .requestMatchers()
          .antMatchers("/openid_connect_login**")
          .and()
         .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
          .and()
        .addFilterBefore(oidcFilter, SecurityContextPersistenceFilter.class)
        .sessionManagement()
          .enableSessionUrlRewriting(false)
          .sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
      //@formatter:on
    }
  }

  @Configuration
  @Order(10)
  public static class ApiEndpointAuthorizationConfig
    extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuth2AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

      http.requestMatchers().antMatchers("/api/**").and().exceptionHandling()
        .authenticationEntryPoint(authenticationEntryPoint).and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);

    }
  }

  @Configuration
  @Order(11)
  public static class ResourceEndpointAuthorizationConfig
    extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuth2AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private CorsFilter corsFilter;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

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
  }

  @Configuration
  @Order(12)
  public static class RegisterEndpointAuthorizationConfig
    extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuth2AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private CorsFilter corsFilter;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

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
  }

  @Configuration
  @Order(13)
  public static class UserInfoEndpointAuthorizationConfig
    extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuth2AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private CorsFilter corsFilter;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

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
  }

  @Configuration
  @Order(14)
  public static class TokenEndpointAuthorizationConfig
    extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuth2AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    @Qualifier("clientUserDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    private CorsFilter corsFilter;

    @Override
    protected void configure(final AuthenticationManagerBuilder auth)
      throws Exception {

      auth.userDetailsService(userDetailsService);
    }

    private ClientCredentialsTokenEndpointFilter clientCredentialsEndpointFilter()
      throws Exception {

      ClientCredentialsTokenEndpointFilter filter = new ClientCredentialsTokenEndpointFilter(
        "/token");
      filter.setAuthenticationManager(authenticationManager());
      return filter;
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

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
      .addFilterBefore(clientCredentialsEndpointFilter(), BasicAuthenticationFilter.class) 
      .addFilterBefore(corsFilter, SecurityContextPersistenceFilter.class)
      .exceptionHandling()
        .authenticationEntryPoint(authenticationEntryPoint)
        .accessDeniedHandler(new OAuth2AccessDeniedHandler())
        .and()
      .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
      // @formatter:on
    }
  }

  @Configuration
  @Order(15)
  public static class IntrospectEndpointAuthorizationConfig
    extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuth2AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    @Qualifier("clientUserDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    private CorsFilter corsFilter;

    @Override
    protected void configure(final AuthenticationManagerBuilder auth)
      throws Exception {

      auth.userDetailsService(userDetailsService);
    }

    private ClientCredentialsTokenEndpointFilter clientCredentialsEndpointFilter()
      throws Exception {

      ClientCredentialsTokenEndpointFilter filter = new ClientCredentialsTokenEndpointFilter(
        "/introspect");
      filter.setAuthenticationManager(authenticationManager());
      return filter;
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

      // @formatter:off
      http
      .requestMatchers()
        .antMatchers("/introspect**")
        .and()
      .httpBasic()
        .authenticationEntryPoint(authenticationEntryPoint)
        .and()
      .addFilterBefore(corsFilter, SecurityContextPersistenceFilter.class)
      .addFilterBefore(clientCredentialsEndpointFilter(), BasicAuthenticationFilter.class) 
      .exceptionHandling()
        .authenticationEntryPoint(authenticationEntryPoint)
        .and()
      .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
      // @formatter:on
    }
  }

  @Configuration
  @Order(16)
  public static class RevokeEndpointAuthorizationConfig
    extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuth2AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    @Qualifier("clientUserDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    private CorsFilter corsFilter;

    @Override
    protected void configure(final AuthenticationManagerBuilder auth)
      throws Exception {

      auth.userDetailsService(userDetailsService);
    }

    private ClientCredentialsTokenEndpointFilter clientCredentialsEndpointFilter()
      throws Exception {

      ClientCredentialsTokenEndpointFilter filter = new ClientCredentialsTokenEndpointFilter(
        "/revoke");
      filter.setAuthenticationManager(authenticationManager());
      return filter;
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

      // @formatter:off
      http
        .requestMatchers()
          .antMatchers("/revoke**")
          .and()
        .httpBasic()
          .authenticationEntryPoint(authenticationEntryPoint)
          .and()
        .addFilterBefore(corsFilter, SecurityContextPersistenceFilter.class)
        .addFilterBefore(clientCredentialsEndpointFilter(), BasicAuthenticationFilter.class) 
        .exceptionHandling()
          .authenticationEntryPoint(authenticationEntryPoint)
          .and()
        .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      ;
      // @formatter:on
    }
  }

  @Configuration
  @Order(17)
  public static class JwkEndpointAuthorizationConfig
    extends WebSecurityConfigurerAdapter {

    @Autowired
    private Http403ForbiddenEntryPoint http403ForbiddenEntryPoint;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

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
      .permitAll();
      // @formatter:on
    }
  }

  @Configuration
  @Order(18)
  @Profile("dev")
  public static class H2ConsoleEndpointAuthorizationConfig
    extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

      HttpSecurity h2Console = http.antMatcher("/h2-console**");
      h2Console.csrf().disable();
      h2Console.httpBasic();
      h2Console.headers().frameOptions().sameOrigin();

      h2Console.authorizeRequests().anyRequest().authenticated();
    }
  }

}
