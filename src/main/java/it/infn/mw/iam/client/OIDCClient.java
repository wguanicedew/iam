package it.infn.mw.iam.client;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.mitre.oauth2.model.ClientDetailsEntity.AuthMethod;
import org.mitre.oauth2.model.RegisteredClient;
import org.mitre.oauth2.service.impl.DefaultOAuth2AuthorizationCodeService;
import org.mitre.openid.connect.client.NamedAdminAuthoritiesMapper;
import org.mitre.openid.connect.client.OIDCAuthenticationProvider;
import org.mitre.openid.connect.client.SubjectIssuerGrantedAuthority;
import org.mitre.openid.connect.client.service.impl.DynamicServerConfigurationService;
import org.mitre.openid.connect.client.service.impl.PlainAuthRequestUrlBuilder;
import org.mitre.openid.connect.client.service.impl.StaticAuthRequestOptionsService;
import org.mitre.openid.connect.client.service.impl.StaticClientConfigurationService;
import org.mitre.openid.connect.client.service.impl.ThirdPartyIssuerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.bind.annotation.RestController;

import it.infn.mw.iam.libs.ExternalAuthenticationSuccessHandler;
import it.infn.mw.iam.libs.IndigoOIDCAuthFilter;
import it.infn.mw.iam.oidc.OIDCUserDetailsService;

@Configuration
@EnableAutoConfiguration
@EnableOAuth2Client
@RestController
public class OIDCClient {

  @Autowired
  private Environment env;

  @Bean(name = "openIdConnectAuthenticationFilter")
  public IndigoOIDCAuthFilter openIdConnectAuthenticationFilter() {

    IndigoOIDCAuthFilter filter = new IndigoOIDCAuthFilter();
    filter.setAuthenticationManager(authenticationManager());
    filter.setIssuerService(accountChooser());
    filter.setServerConfigurationService(dynamicServerConfiguration());
    filter.setClientConfigurationService(staticClientConfiguration());
    filter.setAuthRequestOptionsService(authOptions());
    filter.setAuthRequestUrlBuilder(authRequestBuilder());
    filter.setAuthenticationSuccessHandler(successHandler());

    return filter;
  }

  @Bean
  public AuthenticationSuccessHandler successHandler() {

    return new ExternalAuthenticationSuccessHandler();
  }

  @Bean(name = "OIDCAuthenticationManager")
  public AuthenticationManager authenticationManager() {

    return new ProviderManager(
      Arrays.asList(openIdConnectAuthenticationProvider()));
  }

  @Bean
  public NamedAdminAuthoritiesMapper namedAdmins() {

    NamedAdminAuthoritiesMapper mapper = new NamedAdminAuthoritiesMapper();
    Set<SubjectIssuerGrantedAuthority> adminSet = new LinkedHashSet<SubjectIssuerGrantedAuthority>();

    String[] idpList = env.getProperty("idp.list").split(",");

    for (String idp : idpList) {
      adminSet.add(admin(idp));
    }

    mapper.setAdmins(adminSet);

    return mapper;
  }

  private SubjectIssuerGrantedAuthority admin(String idp) {

    return new SubjectIssuerGrantedAuthority(
      env.getProperty("idp." + idp + ".admin"), idp);
  }

  @Bean
  public OIDCAuthenticationProvider openIdConnectAuthenticationProvider() {

    OIDCAuthenticationProvider provider = new OIDCAuthenticationProvider();
    provider.setAuthoritiesMapper(namedAdmins());

    return provider;
  }

  @Bean
  public DynamicServerConfigurationService dynamicServerConfiguration() {

    return new DynamicServerConfigurationService();
  }

  @Bean
  public ThirdPartyIssuerService accountChooser() {

    ThirdPartyIssuerService issuer = new ThirdPartyIssuerService();
    issuer.setAccountChooserUrl("http://localhost/account-chooser/");

    return issuer;
  }

  private RegisteredClient clientConfig(String idp) {

    String prefix = String.format("idp.%s", idp);

    RegisteredClient config = new RegisteredClient();
    config.setClientId(env.getProperty(prefix + ".client.id"));
    config.setClientSecret(env.getProperty(prefix + ".client.secret"));

    config.setScope(new HashSet<String>(
      Arrays.asList(env.getProperty(prefix + ".auth.scope").split(","))));
    config.setTokenEndpointAuthMethod(AuthMethod.SECRET_BASIC);
    config.setRedirectUris(new HashSet<String>(Arrays
      .asList(env.getProperty(prefix + ".client.redirectUri").split(","))));

    return config;
  }

  @Bean
  public StaticClientConfigurationService staticClientConfiguration() {

    Map<String, RegisteredClient> clients = new LinkedHashMap<String, RegisteredClient>();
    String[] idpList = env.getProperty("idp.list").split(",");

    for (String idp : idpList) {
      clients.put(env.getProperty("idp." + idp + ".issuer"), clientConfig(idp));
    }

    StaticClientConfigurationService config = new StaticClientConfigurationService();
    config.setClients(clients);

    return config;
  }

  @Bean
  public StaticAuthRequestOptionsService authOptions() {

    return new StaticAuthRequestOptionsService();
  }

  @Bean
  public PlainAuthRequestUrlBuilder authRequestBuilder() {

    return new PlainAuthRequestUrlBuilder();
  }

  @Bean
  public DefaultOAuth2AuthorizationCodeService DefaultOAuth2AuthorizationCodeService() {

    return new DefaultOAuth2AuthorizationCodeService();
  }

  @Bean
  public OIDCUserDetailsService oidcUserDetailService() {

    return new OIDCUserDetailsService();
  }

}
