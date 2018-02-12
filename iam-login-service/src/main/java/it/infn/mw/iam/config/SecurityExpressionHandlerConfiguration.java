package it.infn.mw.iam.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import it.infn.mw.iam.api.account.AccountUtils;
import it.infn.mw.iam.core.expression.IamMethodSecurityExpressionHandler;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
public class SecurityExpressionHandlerConfiguration extends GlobalMethodSecurityConfiguration {

  @Autowired
  AccountUtils accountUtils;

  @Autowired
  IamMethodSecurityExpressionHandler expressionHandler;
  
  @Override
  protected MethodSecurityExpressionHandler createExpressionHandler() {

    return expressionHandler;

  }

}
