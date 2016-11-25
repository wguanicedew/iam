package it.infn.mw.iam.api.scim.new_updater.builders;

import static it.infn.mw.iam.api.scim.new_updater.UpdaterType.ACCOUNT_REMOVE_OIDC_ID;
import static it.infn.mw.iam.api.scim.new_updater.UpdaterType.ACCOUNT_REMOVE_SAML_ID;
import static it.infn.mw.iam.api.scim.new_updater.UpdaterType.ACCOUNT_REMOVE_SSH_KEY;

import java.util.Collection;
import java.util.Collections;

import it.infn.mw.iam.api.scim.new_updater.DefaultUpdater;
import it.infn.mw.iam.api.scim.new_updater.Updater;
import it.infn.mw.iam.api.scim.new_updater.UpdaterType;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamOidcId;
import it.infn.mw.iam.persistence.model.IamSamlId;
import it.infn.mw.iam.persistence.model.IamSshKey;
import it.infn.mw.iam.persistence.model.IamX509Certificate;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

public class Removers extends BuilderSupport {

  public Removers(IamAccountRepository repo, IamAccount account) {
    super(repo, account);
  }

  public Updater oidcId(Collection<IamOidcId> toBeRemoved) {

    return new DefaultUpdater<Collection<IamOidcId>>(ACCOUNT_REMOVE_OIDC_ID, account::unlinkOidcIds,
        toBeRemoved, i -> !Collections.disjoint(account.getOidcIds(), i));
  }

  public Updater samlId(Collection<IamSamlId> toBeRemoved) {

    return new DefaultUpdater<Collection<IamSamlId>>(ACCOUNT_REMOVE_SAML_ID, account::unlinkSamlIds,
        toBeRemoved, i -> !Collections.disjoint(account.getSamlIds(), i));
  }

  public Updater sshKey(Collection<IamSshKey> toBeRemoved) {

    return new DefaultUpdater<Collection<IamSshKey>>(ACCOUNT_REMOVE_SSH_KEY, account::unlinkSshKeys,
        toBeRemoved, i -> !Collections.disjoint(account.getSshKeys(), i));
  }

  public Updater x509Certificate(Collection<IamX509Certificate> toBeRemoved) {

    return new DefaultUpdater<Collection<IamX509Certificate>>(
        UpdaterType.ACCOUNT_REMOVE_X509_CERTIFICATE, account::unlinkX509Certificates, toBeRemoved,
        i -> !Collections.disjoint(account.getX509Certificates(), i));
  }
}
