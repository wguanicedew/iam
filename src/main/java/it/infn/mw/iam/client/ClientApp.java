package it.infn.mw.iam.client;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.mitre.oauth2.model.ClientDetailsEntity.AuthMethod;
import org.mitre.oauth2.model.RegisteredClient;
import org.mitre.oauth2.service.impl.DefaultOAuth2AuthorizationCodeService;
import org.mitre.openid.connect.client.NamedAdminAuthoritiesMapper;
import org.mitre.openid.connect.client.OIDCAuthenticationProvider;
import org.mitre.openid.connect.client.SubjectIssuerGrantedAuthority;
import org.mitre.openid.connect.client.service.IssuerService;
import org.mitre.openid.connect.client.service.impl.PlainAuthRequestUrlBuilder;
import org.mitre.openid.connect.client.service.impl.StaticAuthRequestOptionsService;
import org.mitre.openid.connect.client.service.impl.StaticClientConfigurationService;
import org.mitre.openid.connect.client.service.impl.StaticServerConfigurationService;
import org.mitre.openid.connect.client.service.impl.StaticSingleIssuerService;
import org.mitre.openid.connect.config.ServerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.bind.annotation.RestController;

import it.infn.mw.iam.libs.ExternalAuthenticationSuccessHandler;
import it.infn.mw.iam.libs.IndigoOIDCAuthFilter;

@Configuration
@EnableAutoConfiguration
@EnableOAuth2Client
@RestController
public class ClientApp {

  @Autowired
  private Environment env;

  @Bean(name = "openIdConnectAuthenticationFilter")
  public IndigoOIDCAuthFilter openIdConnectAuthenticationFilter() {

    IndigoOIDCAuthFilter filter = new IndigoOIDCAuthFilter();
    filter.setAuthenticationManager(authenticationManager());
    filter.setIssuerService(issuerService());
    filter.setServerConfigurationService(staticServerConfiguration());
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
    mapper.setAdmins(
      new LinkedHashSet<SubjectIssuerGrantedAuthority>(Arrays.asList(admin())));

    return mapper;
  }

  @Bean
  public SubjectIssuerGrantedAuthority admin() {

    return new SubjectIssuerGrantedAuthority("90342.ASDFJWFA",
      env.getProperty("idp.issuer"));
  }

  @Bean
  public OIDCAuthenticationProvider openIdConnectAuthenticationProvider() {

    OIDCAuthenticationProvider provider = new OIDCAuthenticationProvider();
    provider.setAuthoritiesMapper(namedAdmins());

    return provider;
  }

  @Bean
  public IssuerService issuerService() {

    StaticSingleIssuerService issuer = new StaticSingleIssuerService();
    issuer.setIssuer(env.getProperty("idp.issuer"));

    return issuer;
  }

  @Bean
  public ServerConfiguration serverConfig() {

    ServerConfiguration config = new ServerConfiguration();
    config.setIssuer(env.getProperty("idp.issuer"));
    config.setAuthorizationEndpointUri(env.getProperty("idp.authorizeUrl"));
    config.setTokenEndpointUri(env.getProperty("idp.tokenUrl"));
    config.setUserInfoUri(env.getProperty("idp.userinfoUrl"));
    config.setJwksUri(env.getProperty("idp.jwkUrl"));

    return config;
  }

  @Bean
  public StaticServerConfigurationService staticServerConfiguration() {

    Map<String, ServerConfiguration> servers = new LinkedHashMap<>();
    servers.put(env.getProperty("idp.issuer"), serverConfig());

    StaticServerConfigurationService config = new StaticServerConfigurationService();
    config.setServers(servers);

    return config;
  }

  @Bean
  public RegisteredClient clientConfig() {

    RegisteredClient config = new RegisteredClient();
    config.setClientId(env.getProperty("idp.client.id"));
    config.setClientSecret(env.getProperty("idp.client.secret"));

    config.setScope(new HashSet<String>(
      Arrays.asList(env.getProperty("idp.auth.scope").split(","))));
    config.setTokenEndpointAuthMethod(AuthMethod.SECRET_BASIC);
    config.setRedirectUris(new HashSet<String>(
      Arrays.asList(env.getProperty("idp.client.redirectUri").split(","))));

    return config;
  }

  @Bean
  public StaticClientConfigurationService staticClientConfiguration() {

    Map<String, RegisteredClient> clients = new LinkedHashMap<String, RegisteredClient>();
    clients.put(env.getProperty("idp.issuer"), clientConfig());

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
  public StringHttpMessageConverter stringHttpMessageConverter() {

    return new StringHttpMessageConverter();
  }

  @Bean
  public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {

    return new MappingJackson2HttpMessageConverter();
  }

  @Bean
  public DefaultOAuth2AuthorizationCodeService DefaultOAuth2AuthorizationCodeService() {

    return new DefaultOAuth2AuthorizationCodeService();
  }

}
