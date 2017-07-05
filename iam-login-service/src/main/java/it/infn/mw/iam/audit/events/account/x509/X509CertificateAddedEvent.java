package it.infn.mw.iam.audit.events.account.x509;

import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_ADD_X509_CERTIFICATE;

import java.util.Collection;

import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamX509Certificate;

public class X509CertificateAddedEvent extends X509CertificateUpdatedEvent {

  private static final long serialVersionUID = 1L;

  public X509CertificateAddedEvent(Object source, IamAccount account,
      Collection<IamX509Certificate> x509certificates) {
    super(source, account, ACCOUNT_ADD_X509_CERTIFICATE, x509certificates);
  }

}
