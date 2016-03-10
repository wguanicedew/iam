package it.infn.mw.iam.config;

import java.util.Arrays;

import org.mitre.oauth2.repository.AuthenticationHolderRepository;
import org.mitre.oauth2.repository.AuthorizationCodeRepository;
import org.mitre.oauth2.repository.impl.JpaAuthenticationHolderRepository;
import org.mitre.oauth2.repository.impl.JpaAuthorizationCodeRepository;
import org.mitre.oauth2.service.ClientDetailsEntityService;
import org.mitre.oauth2.service.OAuth2TokenEntityService;
import org.mitre.oauth2.service.impl.BlacklistAwareRedirectResolver;
import org.mitre.oauth2.service.impl.DefaultOAuth2AuthorizationCodeService;
import org.mitre.oauth2.service.impl.DefaultOAuth2ClientDetailsEntityService;
import org.mitre.oauth2.service.impl.DefaultOAuth2ProviderTokenService;
import org.mitre.oauth2.token.ChainedTokenGranter;
import org.mitre.oauth2.token.JWTAssertionTokenGranter;
import org.mitre.oauth2.token.StructuredScopeAwareOAuth2RequestValidator;
import org.mitre.openid.connect.request.ConnectOAuth2RequestFactory;
import org.mitre.openid.connect.token.TofuUserApprovalHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.OAuth2RequestValidator;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.endpoint.RedirectResolver;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig
  extends AuthorizationServerConfigurerAdapter {

  @Bean
  RedirectResolver blacklistAwareRedirectResolver(){
    return new BlacklistAwareRedirectResolver();
  }
  
  @Bean
  WebResponseExceptionTranslator webResponseExceptionTranslator(){
    return new DefaultWebResponseExceptionTranslator();
  }
  
  @Bean
  AbstractTokenGranter jwtAssertionTokenGranter() {

    return new JWTAssertionTokenGranter(tokenServices(), clientDetailsService(),
      requestFactory());
  }

  @Bean
  AbstractTokenGranter chainedTokenGranter() {

    return new ChainedTokenGranter(tokenServices(), clientDetailsService(),
      requestFactory());
  }

  @Bean
  OAuth2RequestValidator requestValidator() {

    return new StructuredScopeAwareOAuth2RequestValidator();
  }

  @Bean
  UserApprovalHandler tofuApprovalHandler() {

    return new TofuUserApprovalHandler();
  }

  @Bean
  OAuth2RequestFactory requestFactory() {

    return new ConnectOAuth2RequestFactory(clientDetailsService());
  }

  @Bean
  ClientDetailsEntityService clientDetailsService() {

    return new DefaultOAuth2ClientDetailsEntityService();
  }

  @Bean
  AuthenticationHolderRepository authenticationHolderRepository() {

    return new JpaAuthenticationHolderRepository();
  }

  @Bean
  AuthorizationCodeRepository authorizationCodeRepository() {

    return new JpaAuthorizationCodeRepository();
  }

  @Bean
  AuthorizationCodeServices authorizationCodeServices() {

    return new DefaultOAuth2AuthorizationCodeService();
  }

  @Bean
  OAuth2TokenEntityService tokenServices() {

    return new DefaultOAuth2ProviderTokenService();
  }

  private TokenGranter tokenGranter() {

    AuthorizationServerTokenServices tokenServices = tokenServices();
    AuthorizationCodeServices authorizationCodeServices = authorizationCodeServices();
    ClientDetailsService clientDetailsService = clientDetailsService();
    OAuth2RequestFactory requestFactory = requestFactory();

    return new CompositeTokenGranter(Arrays.<TokenGranter> asList(
      new AuthorizationCodeTokenGranter(tokenServices,
        authorizationCodeServices, clientDetailsService, requestFactory),
      new ImplicitTokenGranter(tokenServices, clientDetailsService,
        requestFactory),
      new RefreshTokenGranter(tokenServices, clientDetailsService,
        requestFactory),
      new ClientCredentialsTokenGranter(tokenServices, clientDetailsService,
        requestFactory),
      chainedTokenGranter(), jwtAssertionTokenGranter()));
  }

  @Override
  public void configure(AuthorizationServerEndpointsConfigurer endpoints)
    throws Exception {

    endpoints.requestValidator(requestValidator())
      .pathMapping("/oauth/token", "/token")
      .pathMapping("/oauth/authorize", "/authorize")
      .tokenServices(tokenServices()).userApprovalHandler(tofuApprovalHandler())
      .requestFactory(requestFactory()).tokenGranter(tokenGranter())
      .authorizationCodeServices(authorizationCodeServices());
  }

  @Override
  public void configure(AuthorizationServerSecurityConfigurer oauthServer)
    throws Exception {

  }

  @Override
  public void configure(ClientDetailsServiceConfigurer clients)
    throws Exception {

    clients.withClientDetails(clientDetailsService());
  }

}
