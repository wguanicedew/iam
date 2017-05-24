package it.infn.mw.iam.authn;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mitre.openid.connect.web.AuthenticationTimeStamper;
import org.slf4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import it.infn.mw.iam.core.util.IamAuthenticationLogger;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

public class TimestamperSuccessHandler implements AuthenticationSuccessHandler {

  public static final Logger LOG = getLogger(TimestamperSuccessHandler.class);
  private final AuthenticationSuccessHandler delegate;
  
  private final IamAccountRepository accountRepository;
  
  public TimestamperSuccessHandler(AuthenticationSuccessHandler delegate,
      IamAccountRepository accountRepository) {
    this.delegate = delegate;
    this.accountRepository = accountRepository;
  }

  protected void setAuthenticationTimestamp(HttpServletRequest request,
      HttpServletResponse response, Authentication authentication) {

    Date timestamp = new Date();
    HttpSession session = request.getSession();
    session.setAttribute(AuthenticationTimeStamper.AUTH_TIMESTAMP, timestamp);
    IamAuthenticationLogger.INSTANCE.logAuthenticationSuccess(authentication);
  }

  protected void touchLastLoginTimeForIamAccount(Authentication authentication){
    if (authentication instanceof OAuth2Authentication){
      OAuth2Authentication oauth = (OAuth2Authentication) authentication;
      if (oauth.getUserAuthentication() != null){
        accountRepository.touchLastLoginTimeForUserWithUsername(oauth.getUserAuthentication().getName());
      }
    }else {
      accountRepository.touchLastLoginTimeForUserWithUsername(authentication.getName());
    }
  }
  
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    delegate.onAuthenticationSuccess(request, response, authentication);
    setAuthenticationTimestamp(request, response, authentication);
    touchLastLoginTimeForIamAccount(authentication);

  }
}
