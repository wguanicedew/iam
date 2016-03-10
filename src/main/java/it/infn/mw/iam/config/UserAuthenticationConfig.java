package it.infn.mw.iam.config;

import org.mitre.openid.connect.web.AuthenticationTimeStamper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

@Configuration
public class UserAuthenticationConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private AuthenticationTimeStamper authenticationTimeStamper;

  @Autowired
  private OAuth2WebSecurityExpressionHandler oAuth2WebSecurityExpressionHandler;

  @Autowired
  @Qualifier("mitreAuthzRequestFilter")
  private GenericFilterBean authorizationRequestFilter;

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth)
    throws Exception {

    auth.jdbcAuthentication();
  }

  @Override
  public void configure(WebSecurity web) throws Exception {

    web.expressionHandler(oAuth2WebSecurityExpressionHandler);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

      // @formatter:off
      http
        .sessionManagement()
          .enableSessionUrlRewriting(false)
          .and()
        .formLogin()
          .loginPage("/login")
          .failureUrl("/login?error=failure")
          .successHandler(authenticationTimeStamper)
          .permitAll()
          .and()
        .authorizeRequests()
          .antMatchers("/**")
          .permitAll()
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
