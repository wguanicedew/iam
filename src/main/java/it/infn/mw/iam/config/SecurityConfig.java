package it.infn.mw.iam.config;

import org.mitre.oauth2.web.CorsFilter;
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
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

@Configuration
@EnableWebSecurity
@EnableResourceServer
public class SecurityConfig {

  @Configuration
  @Order(100)
  public static class UserLoginConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationTimeStamper authenticationTimeStamper;

    @Autowired
    private OAuth2WebSecurityExpressionHandler oAuth2WebSecurityExpressionHandler;

    @Autowired
    @Qualifier("mitreAuthzRequestFilter")
    private GenericFilterBean authorizationRequestFilter;

    @Autowired
    @Qualifier("iamUserDetailsService")
    private UserDetailsService iamUserDetailsService;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)
      throws Exception {
      
      auth.userDetailsService(iamUserDetailsService);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {

      web.expressionHandler(oAuth2WebSecurityExpressionHandler)
        .ignoring().antMatchers("/h2-console**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

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
  @Order(10)
  public static class ApiEndpointAuthorizationConfig
    extends ResourceServerConfigurerAdapter {

    @Autowired
    private OAuth2AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    public void configure(HttpSecurity http) throws Exception {

      // @formatter:off
      http.antMatcher("/api/**")
        .exceptionHandling()
        .authenticationEntryPoint(authenticationEntryPoint).and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);
      // @formatter:on

    }
  }

  @Configuration
  @Order(11)
  public static class ResourceEndpointAuthorizationConfig
    extends ResourceServerConfigurerAdapter {

    @Autowired
    private OAuth2AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private CorsFilter corsFilter;

    @Override
    public void configure(HttpSecurity http) throws Exception {

      // @formatter:off
      http.antMatcher("/resource/**")
        .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
          .and()
        .addFilterBefore(corsFilter, SecurityContextPersistenceFilter.class)
        .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          .and()
        .authorizeRequests()
          .antMatchers("**")
          .permitAll();
      // @formatter:on
    }
  }

  @Configuration
  @Order(12)
  public static class RegisterEndpointAuthorizationConfig
    extends ResourceServerConfigurerAdapter {

    @Autowired
    private OAuth2AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private CorsFilter corsFilter;

    @Override
    public void configure(HttpSecurity http) throws Exception {

      // @formatter:off
      http
        .antMatcher("/register/**")
        .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
          .and()
        .addFilterBefore(corsFilter, SecurityContextPersistenceFilter.class)
        .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          .and()
        .authorizeRequests()
          .antMatchers("/**")
          .permitAll();
      // @formatter:on
    }
  }

  @Configuration
  @Order(13)
  public static class UserInfoEndpointAuthorizationConfig
    extends ResourceServerConfigurerAdapter {

    @Autowired
    private OAuth2AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private CorsFilter corsFilter;

    @Override
    public void configure(HttpSecurity http) throws Exception {

      // @formatter:off
      http
        .antMatcher("/userinfo**")
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
    protected void configure(AuthenticationManagerBuilder auth)
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
    protected void configure(HttpSecurity http) throws Exception {

      // @formatter:off
      http
        .antMatcher("/token")
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
    protected void configure(AuthenticationManagerBuilder auth)
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
    protected void configure(HttpSecurity http) throws Exception {

      // @formatter:off
      http
       .antMatcher("/introspect**")
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
        .and()
      .csrf().disable();
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
    protected void configure(AuthenticationManagerBuilder auth)
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
    protected void configure(HttpSecurity http) throws Exception {

      // @formatter:off
      http
        .antMatcher("/revoke**")
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
  @Order(17)
  public static class JwkEndpointAuthorizationConfig
    extends WebSecurityConfigurerAdapter {

    @Autowired
    private Http403ForbiddenEntryPoint http403ForbiddenEntryPoint;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

      // @formatter:off
      http
        .antMatcher("/jwk**")
      .exceptionHandling()
        .authenticationEntryPoint(http403ForbiddenEntryPoint)
        .and()
      .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
    .authorizeRequests()
      .antMatchers("/**")
      .permitAll();
      // @formatter:on
    }
  }

  @Configuration
  @Order(1)
  @Profile("dev")
  public static class H2ConsoleEndpointAuthorizationConfig
    extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

      HttpSecurity h2Console = http.antMatcher("/h2-console/**");
      h2Console.csrf().disable();
      h2Console.httpBasic();
      h2Console.headers().frameOptions().disable();

      h2Console.authorizeRequests().anyRequest().permitAll();
    }
  }

}
