package it.infn.mw.iam.api.scim.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.infn.mw.iam.api.scim.exception.ScimException;
import it.infn.mw.iam.api.scim.exception.ScimResourceExistsException;
import it.infn.mw.iam.api.scim.model.ScimAddress;
import it.infn.mw.iam.api.scim.model.ScimEmail;
import it.infn.mw.iam.api.scim.model.ScimGroupRef;
import it.infn.mw.iam.api.scim.model.ScimIndigoUser;
import it.infn.mw.iam.api.scim.model.ScimMeta;
import it.infn.mw.iam.api.scim.model.ScimName;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamGroup;
import it.infn.mw.iam.persistence.model.IamOidcId;
import it.infn.mw.iam.persistence.model.IamSamlId;
import it.infn.mw.iam.persistence.model.IamSshKey;
import it.infn.mw.iam.persistence.model.IamUserInfo;
import it.infn.mw.iam.util.ssh.InvalidSshKeyException;
import it.infn.mw.iam.util.ssh.RSAPublicKeyUtils;

@Service
public class UserConverter implements Converter<ScimUser, IamAccount> {

  private final ScimResourceLocationProvider resourceLocationProvider;

  private final AddressConverter addressConverter;

  private final OidcIdConverter oidcIdConverter;
  private final SshKeyConverter sshKeyConverter;
  private final SamlIdConverter samlIdConverter;


  @Autowired
  public UserConverter(ScimResourceLocationProvider rlp, AddressConverter ac, OidcIdConverter oidc,
      SshKeyConverter sshc, SamlIdConverter samlc) {

    this.resourceLocationProvider = rlp;
    this.addressConverter = ac;
    this.oidcIdConverter = oidc;
    this.sshKeyConverter = sshc;
    this.samlIdConverter = samlc;
  }

  @Override
  public IamAccount fromScim(ScimUser scimUser) {

    IamAccount account = new IamAccount();
    IamUserInfo userInfo = new IamUserInfo();

    account.setUuid(scimUser.getId());

    if (scimUser.getActive() != null) {
      account.setActive(scimUser.getActive());
    }

    account.setUsername(scimUser.getUserName());

    if (scimUser.getPassword() != null) {
      account.setPassword(scimUser.getPassword());
    }

    userInfo.setEmail(scimUser.getEmails().get(0).getValue());
    userInfo.setGivenName(scimUser.getName().getGivenName());
    userInfo.setFamilyName(scimUser.getName().getFamilyName());
    userInfo.setMiddleName(scimUser.getName().getMiddleName());
    userInfo.setName(scimUser.getName().getFormatted());

    account.setUserInfo(userInfo);

    if (scimUser.getAddresses() != null && scimUser.getAddresses().size() > 0) {

      userInfo.setAddress(addressConverter.fromScim(scimUser.getAddresses().get(0)));

    }

    if (scimUser.hasOidcIds()) {

      scimUser.getIndigoUser().getOidcIds().forEach(oidcId -> {

        IamOidcId iamOidcId = oidcIdConverter.fromScim(oidcId);

        if (iamOidcId.getAccount() != null) {
          if (account.getUuid() != iamOidcId.getAccount().getUuid()) {

            String errorMessage = String.format("OIDC id %s,%s is already mapped to another user",
                iamOidcId.getIssuer(), iamOidcId.getSubject());

            throw new ScimResourceExistsException(errorMessage);
          }
        } else {

          iamOidcId.setAccount(account);
        }
        account.getOidcIds().add(iamOidcId);
      });
    }

    if (scimUser.hasSshKeys()) {

      scimUser.getIndigoUser().getSshKeys().forEach(sshKey -> {

        IamSshKey iamSshKey = sshKeyConverter.fromScim(sshKey);

        if (iamSshKey.getFingerprint() == null && iamSshKey.getValue() != null) {

          try {
            iamSshKey.setFingerprint(RSAPublicKeyUtils.getSHA256Fingerprint(iamSshKey.getValue()));
          } catch (InvalidSshKeyException e) {
            throw new ScimException(e.getMessage());
          }
        }

        if (iamSshKey.getAccount() != null) {
          if (account.getUuid() != iamSshKey.getAccount().getUuid()) {

            String errorMessage =
                String.format("Ssh key ['%s',%s] is already mapped to another user",
                    iamSshKey.getLabel(), iamSshKey.getFingerprint());

            throw new ScimResourceExistsException(errorMessage);
          }
        } else {

          iamSshKey.setAccount(account);
        }

        if (iamSshKey.getLabel() == null) {

          iamSshKey.setLabel(account.getUsername() + "'s personal ssh key");
        }

        account.getSshKeys().add(iamSshKey);
      });
    }

    if (scimUser.hasSamlIds()) {

      scimUser.getIndigoUser().getSamlIds().forEach(samlId -> {

        IamSamlId iamSamlId = samlIdConverter.fromScim(samlId);

        if (iamSamlId.getAccount() != null) {
          if (account.getUuid() != iamSamlId.getAccount().getUuid()) {

            String errorMessage = String.format("Saml id %s,%s is already mapped to another user",
                iamSamlId.getIdpId(), iamSamlId.getUserId());

            throw new ScimResourceExistsException(errorMessage);
          }
        } else {

          iamSamlId.setAccount(account);
        }
        account.getSamlIds().add(iamSamlId);

      });
    }

    return account;

  }

  @Override
  public ScimUser toScim(IamAccount entity) {

    ScimMeta meta = getScimMeta(entity);
    ScimName name = getScimName(entity);
    ScimIndigoUser indigoUser = getScimIndigoUser(entity);
    ScimAddress address = getScimAddress(entity);

    ScimUser.Builder builder = new ScimUser.Builder(entity.getUsername()).id(entity.getUuid())
      .meta(meta)
      .name(name)
      .active(entity.isActive())
      .displayName(entity.getUsername())
      .locale(entity.getUserInfo().getLocale())
      .nickName(entity.getUserInfo().getNickname())
      .profileUrl(entity.getUserInfo().getProfile())
      .timezone(entity.getUserInfo().getZoneinfo())
      .addEmail(getScimEmail(entity))
      .indigoUserInfo(indigoUser);

    if (address != null) {

      builder.addAddress(address);
    }

    entity.getGroups().forEach(group -> builder.addGroup(getScimGroupRef(group)));
    entity.getX509Certificates().forEach(cert -> builder.buildX509Certificate(cert.getLabel(),
        cert.getCertificate(), cert.isPrimary()));

    return builder.build();
  }

  private ScimMeta getScimMeta(IamAccount entity) {

    return ScimMeta.builder(entity.getCreationTime(), entity.getLastUpdateTime())
      .location(resourceLocationProvider.userLocation(entity.getUuid()))
      .resourceType(ScimUser.RESOURCE_TYPE)
      .build();
  }

  private ScimName getScimName(IamAccount entity) {

    return ScimName.builder()
      .givenName(entity.getUserInfo().getGivenName())
      .familyName(entity.getUserInfo().getFamilyName())
      .middleName(entity.getUserInfo().getMiddleName())
      .build();
  }

  private ScimEmail getScimEmail(IamAccount entity) {

    return ScimEmail.builder().email(entity.getUserInfo().getEmail()).build();
  }

  private ScimIndigoUser getScimIndigoUser(IamAccount entity) {

    ScimIndigoUser.Builder indigoUserBuilder = new ScimIndigoUser.Builder();

    entity.getOidcIds()
      .forEach(oidcId -> indigoUserBuilder.buildOidcId(oidcId.getIssuer(), oidcId.getSubject()));

    entity.getSshKeys().forEach(sshKey -> indigoUserBuilder.buildSshKey(sshKey.getLabel(),
        sshKey.getValue(), sshKey.getFingerprint(), sshKey.isPrimary()));

    entity.getSamlIds()
      .forEach(samlId -> indigoUserBuilder.buildSamlId(samlId.getIdpId(), samlId.getUserId()));

    ScimIndigoUser indigoUser = indigoUserBuilder.build();

    return indigoUser.isEmpty() ? null : indigoUser;
  }

  private ScimGroupRef getScimGroupRef(IamGroup group) {

    return ScimGroupRef.builder()
      .value(group.getUuid())
      .display(group.getName())
      .ref(resourceLocationProvider.groupLocation(group.getUuid()))
      .build();
  }

  private ScimAddress getScimAddress(IamAccount entity) {

    if (entity.getUserInfo() != null && entity.getUserInfo().getAddress() != null) {

      return addressConverter.toScim(entity.getUserInfo().getAddress());
    }
    return null;
  }
}
