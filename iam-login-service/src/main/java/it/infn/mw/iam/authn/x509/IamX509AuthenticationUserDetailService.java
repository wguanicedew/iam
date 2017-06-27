package it.infn.mw.iam.authn.x509;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

import it.infn.mw.iam.authn.ExternalAuthenticationHandlerSupport;
import it.infn.mw.iam.authn.InactiveAccountAuthenticationHander;
import it.infn.mw.iam.authn.util.AuthenticationUtils;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

@Service
public class IamX509AuthenticationUserDetailService
    implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

  public static final Logger LOG =
      LoggerFactory.getLogger(IamX509AuthenticationUserDetailService.class);

  IamAccountRepository accountRepository;
  InactiveAccountAuthenticationHander inactiveAccountHandler;

  @Autowired
  public IamX509AuthenticationUserDetailService(IamAccountRepository accountRepository,
      InactiveAccountAuthenticationHander handler) {
    this.accountRepository = accountRepository;
    this.inactiveAccountHandler = handler;
  }

  protected UserDetails buildUserFromIamAccount(IamAccount account) {
    return AuthenticationUtils.userFromIamAccount(account);
  }

  protected UserDetails buildUserFromX509Credentials(PreAuthenticatedAuthenticationToken token) {
    return new User((String) token.getPrincipal(), "",
        Arrays.asList(ExternalAuthenticationHandlerSupport.EXT_AUTHN_UNREGISTERED_USER_AUTH));
  }

  @Override
  public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token)
      throws UsernameNotFoundException {

    String principal = (String) token.getPrincipal();

    LOG.debug("Loading IAM account for X.509 principal '{}'", principal);

    IamAccount account = accountRepository.findByCertificateSubject(principal).orElseThrow(() -> {
      final String msg = String.format("No IAM account found for X.509 principal '%s'", principal);
      LOG.debug(msg);
      return new UsernameNotFoundException(msg);
    });
    
    LOG.debug("Found IAM account {} linked to principal '{}'", account, principal);

    return buildUserFromIamAccount(account);

  }

}
