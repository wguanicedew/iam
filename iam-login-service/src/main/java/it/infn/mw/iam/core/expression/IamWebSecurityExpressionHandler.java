package it.infn.mw.iam.core.expression;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;

import it.infn.mw.iam.api.account.AccountUtils;
import it.infn.mw.iam.api.requests.GroupRequestUtils;

@Component
public class IamWebSecurityExpressionHandler extends OAuth2WebSecurityExpressionHandler {

  private final AccountUtils accountUtils;
  private final GroupRequestUtils groupRequestUtils;

  @Autowired
  public IamWebSecurityExpressionHandler(AccountUtils accountUtils,
      GroupRequestUtils groupRequestUtils) {
    this.accountUtils = accountUtils;
    this.groupRequestUtils = groupRequestUtils;
  }

  @Override
  protected StandardEvaluationContext createEvaluationContextInternal(Authentication authentication,
      FilterInvocation invocation) {

    StandardEvaluationContext ec =
        super.createEvaluationContextInternal(authentication, invocation);
    ec.setVariable("iam",
        new IamSecurityExpressionMethods(authentication, accountUtils, groupRequestUtils));
    return ec;
  }

}
