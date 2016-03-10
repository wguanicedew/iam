package it.infn.mw.iam.config;

import org.mitre.oauth2.token.StructuredScopeAwareOAuth2RequestValidator;
import org.mitre.openid.connect.token.TofuUserApprovalHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;

@Configuration
@Import(MitreIdConfig.class)
@EnableAuthorizationServer
public class OAuthAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

  @Override
  public void configure(AuthorizationServerEndpointsConfigurer endpoints)
    throws Exception {
  
    endpoints.pathMapping("/oauth/authorize", "/authorize")
    .pathMapping("/oauth/token", "/token")
    .pathMapping("/oauth/error", "/error");
    
    endpoints.userApprovalHandler(new TofuUserApprovalHandler())
      .requestValidator(new StructuredScopeAwareOAuth2RequestValidator());
    
  }
}
