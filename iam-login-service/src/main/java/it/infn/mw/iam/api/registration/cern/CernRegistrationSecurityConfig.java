package it.infn.mw.iam.api.registration.cern;

import static it.infn.mw.iam.authn.ExternalAuthenticationHandlerSupport.EXT_AUTHN_UNREGISTERED_USER_AUTH;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import it.infn.mw.iam.authn.oidc.OidcAccessDeniedHandler;
import it.infn.mw.iam.config.cern.CernProperties;

@Profile("cern")
@Order(99)
@Configuration
public class CernRegistrationSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  CernProperties properties;

  AuthenticationEntryPoint entryPoint() {
    String discoveryId = String.format("/saml/login?idp=%s", properties.getSsoEntityId());
    return new LoginUrlAuthenticationEntryPoint(discoveryId);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    // @formatter:off
    http.requestMatchers()
      .antMatchers("/cern-registration")
      .and()
      .sessionManagement()
        .enableSessionUrlRewriting(false)
      .and()
        .authorizeRequests()
          .antMatchers("/cern-registration")
            .hasAuthority(EXT_AUTHN_UNREGISTERED_USER_AUTH.getAuthority())
      .and()
        .exceptionHandling()
          .accessDeniedHandler(new OidcAccessDeniedHandler())
          .authenticationEntryPoint(entryPoint());
      // @formatter:on
  }
}
