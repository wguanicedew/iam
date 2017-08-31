package it.infn.mw.iam.audit.events.account;

import it.infn.mw.iam.authn.x509.IamX509AuthenticationCredential;
import it.infn.mw.iam.persistence.model.IamAccount;

public class X509CertificateLinkedEvent extends AccountEvent {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private final IamX509AuthenticationCredential x509Certificate;

  public X509CertificateLinkedEvent(Object source, IamAccount account, String message,
      IamX509AuthenticationCredential cred) {
    super(source, account, message);
    this.x509Certificate = cred;
  }

  public IamX509AuthenticationCredential getX509Certificate() {
    return x509Certificate;
  }
}
