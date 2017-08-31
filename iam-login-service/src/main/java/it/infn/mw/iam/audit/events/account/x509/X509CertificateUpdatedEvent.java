package it.infn.mw.iam.audit.events.account.x509;

import java.util.Collection;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.infn.mw.iam.api.scim.updater.UpdaterType;
import it.infn.mw.iam.audit.events.account.AccountUpdatedEvent;
import it.infn.mw.iam.audit.utils.IamX509CertificateSerializer;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamX509Certificate;

public abstract class X509CertificateUpdatedEvent extends AccountUpdatedEvent {

  private static final long serialVersionUID = 1L;

  @JsonSerialize(using = IamX509CertificateSerializer.class)
  private final Collection<IamX509Certificate> x509Certificates;

  public X509CertificateUpdatedEvent(Object source, IamAccount account, UpdaterType type,
      Collection<IamX509Certificate> x509Certificates) {
    super(source, account, type, buildMessage(type, account, x509Certificates));
    this.x509Certificates = x509Certificates;
  }

  protected Collection<IamX509Certificate> getX509certificates() {
    return x509Certificates;
  }

  protected static String buildMessage(UpdaterType t, IamAccount account,
      Collection<IamX509Certificate> x509Certificates) {
    return String.format("%s: username: '%s' values: '%s'", t.getDescription(),
        account.getUsername(), x509Certificates);
  }

}
