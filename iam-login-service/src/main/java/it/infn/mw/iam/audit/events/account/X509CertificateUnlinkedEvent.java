package it.infn.mw.iam.audit.events.account;

import it.infn.mw.iam.persistence.model.IamAccount;

public class X509CertificateUnlinkedEvent extends AccountEvent {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;


  private final String certificateSubject;

  public X509CertificateUnlinkedEvent(Object source, IamAccount account, String message,
      String certificateSubject) {
    super(source, account, message);
    this.certificateSubject = certificateSubject;
  }

  public String getCertificateSubject() {
    return certificateSubject;
  }

}
