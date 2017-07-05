package it.infn.mw.iam.audit.events.account.ssh;

import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_ADD_SSH_KEY;

import java.util.Collection;

import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamSshKey;

public class SshKeyAddedEvent extends SshKeyUpdatedEvent {

  private static final long serialVersionUID = 1L;

  public SshKeyAddedEvent(Object source, IamAccount account, Collection<IamSshKey> sshKeys) {
    super(source, account, ACCOUNT_ADD_SSH_KEY, sshKeys);
  }


}
