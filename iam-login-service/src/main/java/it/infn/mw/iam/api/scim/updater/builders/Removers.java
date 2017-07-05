package it.infn.mw.iam.api.scim.updater.builders;

import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REMOVE_GROUP_MEMBERSHIP;
import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REMOVE_OIDC_ID;
import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REMOVE_PICTURE;
import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REMOVE_SAML_ID;
import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REMOVE_SSH_KEY;
import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REMOVE_X509_CERTIFICATE;

import java.util.Collection;
import java.util.Collections;

import it.infn.mw.iam.api.scim.updater.AccountEventBuilder;
import it.infn.mw.iam.api.scim.updater.AccountUpdater;
import it.infn.mw.iam.api.scim.updater.DefaultAccountUpdater;
import it.infn.mw.iam.audit.events.account.PictureRemovedEvent;
import it.infn.mw.iam.audit.events.account.group.GroupMembershipRemovedEvent;
import it.infn.mw.iam.audit.events.account.oidc.OidcAccountRemovedEvent;
import it.infn.mw.iam.audit.events.account.saml.SamlAccountRemovedEvent;
import it.infn.mw.iam.audit.events.account.ssh.SshKeyRemovedEvent;
import it.infn.mw.iam.audit.events.account.x509.X509CertificateRemovedEvent;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamGroup;
import it.infn.mw.iam.persistence.model.IamOidcId;
import it.infn.mw.iam.persistence.model.IamSamlId;
import it.infn.mw.iam.persistence.model.IamSshKey;
import it.infn.mw.iam.persistence.model.IamUserInfo;
import it.infn.mw.iam.persistence.model.IamX509Certificate;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

public class Removers extends AccountBuilderSupport {

  final AccountEventBuilder<Collection<IamOidcId>, OidcAccountRemovedEvent> buildOidcAccountRemovedEvent =
      (source, a, v) -> {
        return new OidcAccountRemovedEvent(source, a, v);
      };

  final AccountEventBuilder<Collection<IamSamlId>, SamlAccountRemovedEvent> buildSamlAccountRemovedEvent =
      (source, a, v) -> {
        return new SamlAccountRemovedEvent(source, a, v);
      };

  final AccountEventBuilder<Collection<IamSshKey>, SshKeyRemovedEvent> buildSshKeyRemovedEvent =
      (source, a, v) -> {
        return new SshKeyRemovedEvent(source, a, v);
      };

  final AccountEventBuilder<Collection<IamX509Certificate>, X509CertificateRemovedEvent> buildX509CertificateRemovedEvent =
      (source, a, v) -> {
        return new X509CertificateRemovedEvent(source, a, v);
      };

  final AccountEventBuilder<Collection<IamGroup>, GroupMembershipRemovedEvent> buildGroupMembershipRemovedEvent =
      (source, a, v) -> {
        return new GroupMembershipRemovedEvent(source, a, v);
      };

  final AccountEventBuilder<String, PictureRemovedEvent> buildPictureRemovedEvent =
      (source, a, v) -> {
        return new PictureRemovedEvent(source, a, v);
      };

  public Removers(IamAccountRepository repo, IamAccount account) {
    super(repo, account);
  }

  public AccountUpdater oidcId(Collection<IamOidcId> toBeRemoved) {

    return new DefaultAccountUpdater<Collection<IamOidcId>, OidcAccountRemovedEvent>(account,
        ACCOUNT_REMOVE_OIDC_ID, account::unlinkOidcIds, toBeRemoved,
        i -> !Collections.disjoint(account.getOidcIds(), i), buildOidcAccountRemovedEvent);
  }

  public AccountUpdater samlId(Collection<IamSamlId> toBeRemoved) {

    return new DefaultAccountUpdater<Collection<IamSamlId>, SamlAccountRemovedEvent>(account,
        ACCOUNT_REMOVE_SAML_ID, account::unlinkSamlIds, toBeRemoved,
        i -> !Collections.disjoint(account.getSamlIds(), i), buildSamlAccountRemovedEvent);
  }

  public AccountUpdater sshKey(Collection<IamSshKey> toBeRemoved) {

    return new DefaultAccountUpdater<Collection<IamSshKey>, SshKeyRemovedEvent>(account,
        ACCOUNT_REMOVE_SSH_KEY, account::unlinkSshKeys, toBeRemoved,
        i -> !Collections.disjoint(account.getSshKeys(), i), buildSshKeyRemovedEvent);
  }

  public AccountUpdater x509Certificate(Collection<IamX509Certificate> toBeRemoved) {

    return new DefaultAccountUpdater<Collection<IamX509Certificate>, X509CertificateRemovedEvent>(
        account, ACCOUNT_REMOVE_X509_CERTIFICATE, account::unlinkX509Certificates, toBeRemoved,
        i -> !Collections.disjoint(account.getX509Certificates(), i),
        buildX509CertificateRemovedEvent);
  }

  public AccountUpdater group(Collection<IamGroup> toBeRemoved) {

    return new DefaultAccountUpdater<Collection<IamGroup>, GroupMembershipRemovedEvent>(account,
        ACCOUNT_REMOVE_GROUP_MEMBERSHIP, account::unlinkMembers, toBeRemoved,
        i -> !Collections.disjoint(account.getGroups(), i), buildGroupMembershipRemovedEvent);
  }

  public AccountUpdater picture(String picture) {
    final IamUserInfo ui = account.getUserInfo();
    return new DefaultAccountUpdater<String, PictureRemovedEvent>(account, ACCOUNT_REMOVE_PICTURE,
        ui::getPicture, ui::setPicture, null, buildPictureRemovedEvent);
  }
}
