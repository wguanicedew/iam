package it.infn.mw.iam.config;

import org.mitre.oauth2.web.CorsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

@Configuration
public class StaticResourcesSecurityConfig extends WebSecurityConfigurerAdapter{

  @Autowired
  private CorsFilter corsFilter;

  @Autowired
  private Http403ForbiddenEntryPoint http403ForbiddenEntryPoint;
  
  @Override
  protected void configure(HttpSecurity http) throws Exception {
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
    
    super.configure(http);
  }

}
