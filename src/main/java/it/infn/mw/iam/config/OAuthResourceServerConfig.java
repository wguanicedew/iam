package it.infn.mw.iam.config;

import org.mitre.oauth2.web.CorsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@EnableResourceServer
public class OAuthResourceServerConfig extends ResourceServerConfigurerAdapter {

  @Bean
  public OncePerRequestFilter corsFilter() {

    return new CorsFilter();
  }

  @Bean
  public OAuth2AuthenticationEntryPoint oauthAuthenticationEntryPoint() {

    OAuth2AuthenticationEntryPoint entryPoint = new OAuth2AuthenticationEntryPoint();
    entryPoint.setRealmName("openidconnect");
    return entryPoint;
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {

    // @formatter:off
    http
      .antMatcher(
        "/#{T(org.mitre.openid.connect.web.DynamicClientRegistrationEndpoint).URL}/**")
      .httpBasic()
      .authenticationEntryPoint(oauthAuthenticationEntryPoint())
      .and()
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      .and()
      .addFilterAfter(corsFilter(), SecurityContextPersistenceFilter.class).httpBasic()
      .and().authorizeRequests().antMatchers("/resources/**").permitAll();

    http
      .antMatcher(
        "/#{T(org.mitre.openid.connect.web.ProtectedResourceRegistrationEndpoint).URL}/**")
      .httpBasic()
      .authenticationEntryPoint(oauthAuthenticationEntryPoint())
      .and()
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      .and()
      .addFilterAfter(corsFilter(), SecurityContextPersistenceFilter.class).httpBasic()
      .and().authorizeRequests().antMatchers("/resources/**").permitAll();

    http
      .antMatcher("/#{T(org.mitre.openid.connect.web.UserInfoEndpoint).URL}**")
      .httpBasic()
      .authenticationEntryPoint(oauthAuthenticationEntryPoint())
      .and()
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      .and()
      .addFilterAfter(corsFilter(), SecurityContextPersistenceFilter.class);

    http
      .antMatcher(
        "/#{T(org.mitre.openid.connect.web.RootController).API_URL}/**")
      .httpBasic()
      .authenticationEntryPoint(oauthAuthenticationEntryPoint())
      .and()
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
      .and().addFilterAfter(corsFilter(), SecurityContextPersistenceFilter.class);
    // @formatter:on

    
    super.configure(http);
  }

}
