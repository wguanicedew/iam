package it.infn.mw.iam.core.expression;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;
import org.springframework.stereotype.Component;

import it.infn.mw.iam.api.account.AccountUtils;

@Component
public class IamMethodSecurityExpressionHandler extends OAuth2MethodSecurityExpressionHandler {

  private final AccountUtils accountUtils;
  
  @Autowired
  public IamMethodSecurityExpressionHandler(AccountUtils accountUtils) {
    this.accountUtils = accountUtils;
  }

  @Override
  public StandardEvaluationContext createEvaluationContextInternal(Authentication authentication,
      MethodInvocation mi) {

    StandardEvaluationContext ec = super.createEvaluationContextInternal(authentication, mi);
    ec.setVariable("iam", new IamSecurityExpressionMethods(authentication, accountUtils));
    return ec;
  }

}
