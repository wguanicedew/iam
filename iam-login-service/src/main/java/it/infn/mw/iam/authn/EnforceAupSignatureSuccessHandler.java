package it.infn.mw.iam.authn;

import static it.infn.mw.iam.core.web.EnforceAupFilter.REQUESTING_SIGNATURE;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mitre.openid.connect.web.AuthenticationTimeStamper;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import it.infn.mw.iam.api.account.AccountUtils;
import it.infn.mw.iam.api.aup.AUPSignatureCheckService;
import it.infn.mw.iam.core.util.IamAuthenticationLogger;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

public class EnforceAupSignatureSuccessHandler implements AuthenticationSuccessHandler {

  private final AuthenticationSuccessHandler delegate;
  private final AUPSignatureCheckService service;
  private final AccountUtils accountUtils;
  private final IamAccountRepository accountRepo;

  public EnforceAupSignatureSuccessHandler(AuthenticationSuccessHandler delegate,
      AUPSignatureCheckService service, AccountUtils utils, IamAccountRepository accountRepo) {
    this.delegate = delegate;
    this.service = service;
    this.accountUtils = utils;
    this.accountRepo = accountRepo;
  }

  private IamAccount lookupAuthenticatedUser(Authentication auth) {
    return accountUtils.getAuthenticatedUserAccount()
      .orElseThrow(() -> new IllegalArgumentException(
          "IamAccount not found for succesfull user authentication: " + auth.getName()));
  }

  protected void setAuthenticationTimestamp(HttpServletRequest request,
      Authentication authentication) {

    Date timestamp = new Date();
    HttpSession session = request.getSession();
    session.setAttribute(AuthenticationTimeStamper.AUTH_TIMESTAMP, timestamp);
    IamAuthenticationLogger.INSTANCE.logAuthenticationSuccess(authentication);
  }

  protected void touchLastLoginTimeForIamAccount(Authentication authentication) {
    if (authentication instanceof OAuth2Authentication) {
      OAuth2Authentication oauth = (OAuth2Authentication) authentication;
      if (oauth.getUserAuthentication() != null) {
        accountRepo.touchLastLoginTimeForUserWithUsername(oauth.getUserAuthentication().getName());
      }
    } else {
      accountRepo.touchLastLoginTimeForUserWithUsername(authentication.getName());
    }
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication auth) throws IOException, ServletException {

    HttpSession session = request.getSession(false);
    
    setAuthenticationTimestamp(request, auth);
    touchLastLoginTimeForIamAccount(auth);
    
    IamAccount authenticatedUser = lookupAuthenticatedUser(auth);
    
    if (!service.needsAupSignature(authenticatedUser)) {
      delegate.onAuthenticationSuccess(request, response, auth);
      return;
    }

    session.setAttribute(REQUESTING_SIGNATURE, true);
    response.sendRedirect("/iam/aup/sign");

  }

}
