package it.infn.mw.pr;

import org.mitre.oauth2.introspectingfilter.IntrospectingTokenService;
import org.mitre.oauth2.introspectingfilter.service.impl.SimpleIntrospectionAuthorityGranter;
import org.mitre.oauth2.introspectingfilter.service.impl.StaticIntrospectionConfigurationService;
import org.mitre.oauth2.model.RegisteredClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ProtectedResourceConfiguration extends ResourceServerConfigurerAdapter {

  @Autowired
  ResourceServerProperties props;

  @Value("${iam.client-id}")
  String iamClientId;

  @Value("${iam.client-secret}")
  String iamClientSecret;

  @Value("${iam.token-info-uri}")
  String iamTokenInfoUri;

  public SimpleIntrospectionAuthorityGranter authorityGranter() {
    return new SimpleIntrospectionAuthorityGranter();
  }

  public RegisteredClient clientConfig() {

    RegisteredClient rc = new RegisteredClient();
    rc.setClientId(iamClientId);
    rc.setClientSecret(iamClientSecret);
    return rc;
  }

  public StaticIntrospectionConfigurationService introspectionConfigService() {

    StaticIntrospectionConfigurationService cs = new StaticIntrospectionConfigurationService();
    cs.setIntrospectionUrl(iamTokenInfoUri);
    cs.setClientConfiguration(clientConfig());
    return cs;
  }

  @Bean
  public IntrospectingTokenService tokenService() {

    IntrospectingTokenService its = new IntrospectingTokenService();
    its.setIntrospectionConfigurationService(introspectionConfigService());
    its.setIntrospectionAuthorityGranter(authorityGranter());
    return its;
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {

    http.authorizeRequests().anyRequest().authenticated().and().csrf().disable().sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

  }

  @Override
  public void configure(ResourceServerSecurityConfigurer resources) throws Exception {

    resources.tokenServices(tokenService());

  }

}
