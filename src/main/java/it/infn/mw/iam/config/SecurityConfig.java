package it.infn.mw.iam.config;

import org.mitre.openid.connect.web.AuthenticationTimeStamper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private AuthenticationTimeStamper authenticationTimeStamper;
  
  @Override
  public void configure(WebSecurity web) throws Exception {

    web.ignoring().antMatchers("/resources/**", "/images/**");
    web.expressionHandler(new OAuth2WebSecurityExpressionHandler());

  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {

    auth.inMemoryAuthentication().withUser("admin").password("password")
      .roles("ADMIN", "USER");

  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    // @formatter:off
    http
      .authorizeRequests()
      .antMatchers("/h2-console/**").permitAll()
      .and()
      .formLogin()
        .loginPage("/login")
        .failureUrl("/login?error=failure")
        .successHandler(authenticationTimeStamper)
        .permitAll()
        .and()
      .logout()
        .logoutUrl("/logout")
        .permitAll()
        .and()
      .anonymous()
        .and()
      .headers()
        .frameOptions().deny()
        .and()
      .csrf()
        .disable();
    
    // @formatter:on    
  }

}
