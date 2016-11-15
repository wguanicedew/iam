package it.infn.mw.iam.api.scim.updater;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.infn.mw.iam.api.scim.exception.ScimPatchOperationNotSupported;
import it.infn.mw.iam.api.scim.model.ScimPatchOperation;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.updater.user.ActiveUpdater;
import it.infn.mw.iam.api.scim.updater.user.AddressUpdater;
import it.infn.mw.iam.api.scim.updater.user.EmailUpdater;
import it.infn.mw.iam.api.scim.updater.user.IndigoUserUpdater;
import it.infn.mw.iam.api.scim.updater.user.NameUpdater;
import it.infn.mw.iam.api.scim.updater.user.PasswordUpdater;
import it.infn.mw.iam.api.scim.updater.user.PhotoUpdater;
import it.infn.mw.iam.api.scim.updater.user.UserUpdaterCollection;
import it.infn.mw.iam.api.scim.updater.user.UsernameUpdater;
import it.infn.mw.iam.api.scim.updater.user.X509CertificateUpdater;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

@Component
public class ScimUserUpdater {

  @Autowired
  private IamAccountRepository accountRepository;

  private UserUpdaterCollection addUpdaters;
  private UserUpdaterCollection replaceUpdaters;
  private UserUpdaterCollection removeUpdaters;

  @Autowired
  public ScimUserUpdater(NameUpdater nameUpdater, AddressUpdater addressUpdater,
      UsernameUpdater usernameUpdater, PasswordUpdater passwordUpdater, PhotoUpdater photoUpdater,
      EmailUpdater emailUpdater, ActiveUpdater activeUpdater,
      X509CertificateUpdater x509CertificateUpdater, IndigoUserUpdater indigoUserUpdater) {

    addUpdaters = new UserUpdaterCollection();
    addUpdaters.addUpdater(nameUpdater);
    addUpdaters.addUpdater(addressUpdater);
    addUpdaters.addUpdater(usernameUpdater);
    addUpdaters.addUpdater(passwordUpdater);
    addUpdaters.addUpdater(photoUpdater);
    addUpdaters.addUpdater(emailUpdater);
    addUpdaters.addUpdater(activeUpdater);
    addUpdaters.addUpdater(x509CertificateUpdater);
    addUpdaters.addUpdater(indigoUserUpdater);

    replaceUpdaters = new UserUpdaterCollection();
    replaceUpdaters.addUpdater(nameUpdater);
    replaceUpdaters.addUpdater(addressUpdater);
    replaceUpdaters.addUpdater(usernameUpdater);
    replaceUpdaters.addUpdater(passwordUpdater);
    replaceUpdaters.addUpdater(photoUpdater);
    replaceUpdaters.addUpdater(emailUpdater);
    replaceUpdaters.addUpdater(activeUpdater);
    replaceUpdaters.addUpdater(x509CertificateUpdater);
    replaceUpdaters.addUpdater(indigoUserUpdater);

    removeUpdaters = new UserUpdaterCollection();
    removeUpdaters.addUpdater(photoUpdater);
    removeUpdaters.addUpdater(x509CertificateUpdater);
    removeUpdaters.addUpdater(indigoUserUpdater);
  }

  public void update(IamAccount account, List<ScimPatchOperation<ScimUser>> operations) {

    for (ScimPatchOperation<ScimUser> op: operations) {

      if (op.getPath() != null) {
        throw new ScimPatchOperationNotSupported("Path " + op.getPath() + " is not supported");
      }

      switch (op.getOp()) {
        case add:
          if (addUpdaters.add(account, op.getValue())) {
            account.touch();
            accountRepository.save(account);
          }
          break;
        case remove:
          if (removeUpdaters.remove(account, op.getValue())) {
            account.touch();
            accountRepository.save(account);
          }
          break;
        case replace:
          if (replaceUpdaters.replace(account, op.getValue())) {
            account.touch();
            accountRepository.save(account);
          }
          break;
      }
    }
  }
}
