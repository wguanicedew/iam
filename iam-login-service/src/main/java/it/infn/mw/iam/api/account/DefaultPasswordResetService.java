package it.infn.mw.iam.api.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.infn.mw.iam.api.scim.exception.ScimResourceNotFoundException;
import it.infn.mw.iam.notification.NotificationService;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.registration.TokenGenerator;

@Service
public class DefaultPasswordResetService implements PasswordResetService {

  @Autowired
  private IamAccountRepository accountRepository;

  @Autowired
  private NotificationService notificationService;

  @Autowired
  private TokenGenerator tokenGenerator;


  @Override
  public Boolean checkResetKey(String resetKey) {

    IamAccount account = accountRepository.findByResetKey(resetKey)
      .orElseThrow(() -> new ScimResourceNotFoundException(
          String.format("No account found for reset_key [%s]", resetKey)));

    return (account.getUserInfo().getEmailVerified() && account.isActive());
  }

  @Override
  public void changePassword(String resetKey, String password) {
    // TODO: missing implementation

    // find iamaccount

    // update password

    // nullify resetKey?
  }

  @Override
  public void forgotPassword(String email) {
    IamAccount account =
        accountRepository.findByEmail(email).orElseThrow(() -> new ScimResourceNotFoundException(
            String.format("No account found for the email address [%s]", email)));

    String resetKey = tokenGenerator.generateToken();
    account.setResetKey(resetKey);

    notificationService.createResetPasswordMessage(account);
  }

}
