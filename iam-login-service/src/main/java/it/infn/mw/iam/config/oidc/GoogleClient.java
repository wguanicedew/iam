package it.infn.mw.iam.config.oidc;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.mitre.oauth2.model.RegisteredClient;
import org.mitre.openid.connect.client.OIDCAuthenticationProvider;
import org.mitre.openid.connect.client.service.IssuerService;
import org.mitre.openid.connect.client.service.impl.DynamicServerConfigurationService;
import org.mitre.openid.connect.client.service.impl.PlainAuthRequestUrlBuilder;
import org.mitre.openid.connect.client.service.impl.StaticAuthRequestOptionsService;
import org.mitre.openid.connect.client.service.impl.StaticClientConfigurationService;
import org.mitre.openid.connect.client.service.impl.StaticSingleIssuerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import it.infn.mw.iam.authn.ExternalAuthenticationFailureHandler;
import it.infn.mw.iam.authn.TimestamperSuccessHandler;
import it.infn.mw.iam.authn.oidc.OidcAuthenticationProvider;
import it.infn.mw.iam.authn.oidc.OidcClientFilter;
import it.infn.mw.iam.authn.oidc.OidcExceptionMessageHelper;
import it.infn.mw.iam.authn.oidc.service.DefaultOidcUserDetailsService;
import it.infn.mw.iam.authn.oidc.service.OidcUserDetailsService;

@Configuration
@Profile("google")
public class GoogleClient {

  @Value("${iam.baseUrl}")
  private String iamBaseUrl;

  @Autowired
  private GoogleClientProperties googleClientProperties;

  @Bean
  public FilterRegistrationBean disabledAutomaticOidcFilterRegistration(
      OidcClientFilter f) {

    FilterRegistrationBean b = new FilterRegistrationBean(f);
    b.setEnabled(false);
    return b;
  }

  @Bean(name = "openIdConnectAuthenticationFilter")
  public OidcClientFilter openIdConnectAuthenticationFilterCanl() {

    OidcClientFilter filter = new OidcClientFilter();
    filter.setAuthenticationManager(authenticationManager());
    filter.setIssuerService(googleIssuerService());
    filter.setServerConfigurationService(dynamicServerConfiguration());
    filter.setClientConfigurationService(staticClientConfiguration());
    filter.setAuthRequestOptionsService(authOptions());
    filter.setAuthRequestUrlBuilder(authRequestBuilder());
    filter.setAuthenticationSuccessHandler(successHandler());
    filter.setHttpRequestFactory(new HttpComponentsClientHttpRequestFactory());
    filter.setAuthenticationFailureHandler(
        new ExternalAuthenticationFailureHandler(new OidcExceptionMessageHelper()));

    return filter;
  }

  @Bean(name = "externalAuthenticationSuccessHandler")
  public AuthenticationSuccessHandler successHandler() {

    return new TimestamperSuccessHandler(new SavedRequestAwareAuthenticationSuccessHandler());

  }

  @Bean(name = "OIDCAuthenticationManager")
  public AuthenticationManager authenticationManager() {

    return new ProviderManager(Arrays.asList(openIdConnectAuthenticationProvider()));
  }

  @Bean
  public OIDCAuthenticationProvider openIdConnectAuthenticationProvider() {

    OidcAuthenticationProvider provider = new OidcAuthenticationProvider(userDetailService());
    return provider;
  }

  private IssuerService googleIssuerService() {

    StaticSingleIssuerService issuerService = new StaticSingleIssuerService();
    issuerService.setIssuer(googleClientProperties.getIssuer());

    return issuerService;
  }

  private DynamicServerConfigurationService dynamicServerConfiguration() {

    return new DynamicServerConfigurationService();
  }

  private StaticClientConfigurationService staticClientConfiguration() {

    Map<String, RegisteredClient> clients = new LinkedHashMap<String, RegisteredClient>();

    clients.put(googleClientProperties.getIssuer(), googleClientProperties);

    StaticClientConfigurationService config = new StaticClientConfigurationService();
    config.setClients(clients);

    return config;
  }

  private StaticAuthRequestOptionsService authOptions() {

    return new StaticAuthRequestOptionsService();
  }

  private PlainAuthRequestUrlBuilder authRequestBuilder() {

    return new PlainAuthRequestUrlBuilder();
  }

  @Bean
  public OidcUserDetailsService userDetailService() {

    return new DefaultOidcUserDetailsService();
  }
}
