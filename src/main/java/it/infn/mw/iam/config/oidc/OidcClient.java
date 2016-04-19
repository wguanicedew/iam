package it.infn.mw.iam.config.oidc;

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
import org.mitre.openid.connect.client.service.impl.HybridServerConfigurationService;
import org.mitre.openid.connect.client.service.impl.PlainAuthRequestUrlBuilder;
import org.mitre.openid.connect.client.service.impl.StaticAuthRequestOptionsService;
import org.mitre.openid.connect.client.service.impl.StaticClientConfigurationService;
import org.mitre.openid.connect.client.service.impl.ThirdPartyIssuerService;
import org.mitre.openid.connect.config.ServerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import it.infn.mw.iam.oidc.AccountChooserConfigurationProvider;
import it.infn.mw.iam.oidc.ExternalAuthenticationSuccessHandler;
import it.infn.mw.iam.oidc.IamAccountChooserConfigurationProvider;
import it.infn.mw.iam.oidc.IamOidcAuthenticationFilter;
import it.infn.mw.iam.oidc.service.OidcUserDetailsService;

@Configuration
@Profile("oidc")
public class OidcClient {

  @Value("${iam.baseUrl}")
  private String iamBaseUrl;

  @Autowired
  private Environment env;

  @Bean(name = "openIdConnectAuthenticationFilter")
  public IamOidcAuthenticationFilter openIdConnectAuthenticationFilter() {

    IamOidcAuthenticationFilter filter = new IamOidcAuthenticationFilter();
    filter.setAuthenticationManager(authenticationManager());
    filter.setIssuerService(accountChooser());
    filter.setServerConfigurationService(dynamicServerConfiguration());
    filter.setClientConfigurationService(staticClientConfiguration());
    filter.setAuthRequestOptionsService(authOptions());
    filter.setAuthRequestUrlBuilder(authRequestBuilder());
    filter.setAuthenticationSuccessHandler(successHandler());

    return filter;
  }

  @Bean(name = "externalAuthenticationSuccessHandler")
  public AuthenticationSuccessHandler successHandler() {

    return new ExternalAuthenticationSuccessHandler();
  }

  @Bean(name = "OIDCAuthenticationManager")
  public AuthenticationManager authenticationManager() {

    return new ProviderManager(
      Arrays.asList(openIdConnectAuthenticationProvider()));
  }

  private NamedAdminAuthoritiesMapper namedAdmins() {

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

  private DynamicServerConfigurationService dynamicServerConfiguration() {

    return new DynamicServerConfigurationService();
  }

  @Bean
  public HybridServerConfigurationService hybridServerConfigurationService() {

    HybridServerConfigurationService config = new HybridServerConfigurationService();
    Map<String, ServerConfiguration> servers = readServerConfiguration();

    config.setServers(servers);
    return config;
  }

  private Map<String, ServerConfiguration> readServerConfiguration() {

    Map<String, ServerConfiguration> servers = new LinkedHashMap<String, ServerConfiguration>();

    String[] idpList = env.getProperty("idp.list").split(",");

    for (String idp : idpList) {
      String prefix = String.format("idp.%s", idp);
      String issuer = env.getProperty(prefix + ".issuer");
      String authzUrl = env.getProperty(prefix + ".authorizeUrl");
      String tokenUrl = env.getProperty(prefix + ".tokenUrl");
      String userinfoUrl = env.getProperty(prefix + ".userinfoUrl");

      if (authzUrl != null && tokenUrl != null && userinfoUrl != null) {
        ServerConfiguration config = new ServerConfiguration();
        config.setIssuer(issuer);
        config.setAuthorizationEndpointUri(authzUrl);
        config.setTokenEndpointUri(tokenUrl);
        config.setUserInfoUri(userinfoUrl);

        servers.put(issuer, config);
      }

    }

    return servers;
  }

  private ThirdPartyIssuerService accountChooser() {

    ThirdPartyIssuerService issuer = new ThirdPartyIssuerService();
    issuer.setAccountChooserUrl(
      iamBaseUrl + "/resources/account-chooser/index.html");

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

  private StaticClientConfigurationService staticClientConfiguration() {

    Map<String, RegisteredClient> clients = new LinkedHashMap<String, RegisteredClient>();
    String[] idpList = env.getProperty("idp.list").split(",");

    for (String idp : idpList) {
      clients.put(env.getProperty("idp." + idp + ".issuer"), clientConfig(idp));
    }

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
  public DefaultOAuth2AuthorizationCodeService DefaultOAuth2AuthorizationCodeService() {

    return new DefaultOAuth2AuthorizationCodeService();
  }

  @Bean
  public AccountChooserConfigurationProvider iamAccountChooserConfiguration() {

    return new IamAccountChooserConfigurationProvider(iamBaseUrl);
  }
  
  @Bean
  public OidcUserDetailsService userDetailService(){
    return new OidcUserDetailsService();
  }
}
