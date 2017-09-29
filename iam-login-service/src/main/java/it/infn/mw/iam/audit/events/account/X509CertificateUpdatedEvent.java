package it.infn.mw.iam.audit.events.account;

import it.infn.mw.iam.authn.x509.IamX509AuthenticationCredential;
import it.infn.mw.iam.persistence.model.IamAccount;

public class X509CertificateUpdatedEvent extends X509CertificateLinkedEvent {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public X509CertificateUpdatedEvent(Object source, IamAccount account, String message,
      IamX509AuthenticationCredential cred) {
    super(source, account, message, cred);
  }

}
