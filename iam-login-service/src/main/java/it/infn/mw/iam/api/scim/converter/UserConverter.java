package it.infn.mw.iam.api.scim.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.infn.mw.iam.api.scim.model.ScimAddress;
import it.infn.mw.iam.api.scim.model.ScimEmail;
import it.infn.mw.iam.api.scim.model.ScimGroupRef;
import it.infn.mw.iam.api.scim.model.ScimIndigoUser;
import it.infn.mw.iam.api.scim.model.ScimMeta;
import it.infn.mw.iam.api.scim.model.ScimName;
import it.infn.mw.iam.api.scim.model.ScimOidcId;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.model.ScimX509Certificate;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamGroup;
import it.infn.mw.iam.persistence.model.IamOidcId;
import it.infn.mw.iam.persistence.model.IamUserInfo;
import it.infn.mw.iam.persistence.model.IamX509Certificate;

@Service
public class UserConverter implements Converter<ScimUser, IamAccount> {

  private final ScimResourceLocationProvider resourceLocationProvider;

  private final AddressConverter addressConverter;
  private final X509CertificateConverter certificateConverter;

  @Autowired
  public UserConverter(ScimResourceLocationProvider rlp, AddressConverter ac,
      X509CertificateConverter cc) {

    resourceLocationProvider = rlp;
    addressConverter = ac;
    certificateConverter = cc;
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

    userInfo.setEmail(scimUser.getEmails().get(0).getValue());
    userInfo.setGivenName(scimUser.getName().getGivenName());
    userInfo.setFamilyName(scimUser.getName().getFamilyName());
    userInfo.setMiddleName(scimUser.getName().getMiddleName());
    userInfo.setName(scimUser.getName().getFormatted());

    account.setUserInfo(userInfo);

    if (scimUser.getAddresses() != null && scimUser.getAddresses().size() > 0) {

      userInfo.setAddress(addressConverter.fromScim(scimUser.getAddresses().get(0)));

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
        .meta(meta).name(name).active(entity.isActive()).displayName(entity.getUsername())
        .locale(entity.getUserInfo().getLocale()).nickName(entity.getUserInfo().getNickname())
        .profileUrl(entity.getUserInfo().getProfile()).timezone(entity.getUserInfo().getZoneinfo())
        .addEmail(getScimEmail(entity)).indigoUserInfo(indigoUser);

    if (address != null) {

      builder.addAddress(address);
    }

    for (IamGroup group : entity.getGroups()) {

      builder.addGroup(getScimGroupRef(group));
    }

    for (IamX509Certificate cert : entity.getX509Certificates()) {

      builder.addX509Certificate(getScimX509Certificate(cert));
    }
    return builder.build();
  }

  private ScimMeta getScimMeta(IamAccount entity) {

    return ScimMeta.builder(entity.getCreationTime(), entity.getLastUpdateTime())
        .location(resourceLocationProvider.userLocation(entity.getUuid()))
        .resourceType(ScimUser.RESOURCE_TYPE).build();
  }

  private ScimName getScimName(IamAccount entity) {

    return ScimName.builder().givenName(entity.getUserInfo().getGivenName())
        .familyName(entity.getUserInfo().getFamilyName())
        .middleName(entity.getUserInfo().getMiddleName()).build();
  }

  private ScimEmail getScimEmail(IamAccount entity) {

    return ScimEmail.builder().email(entity.getUserInfo().getEmail()).build();
  }

  private ScimIndigoUser getScimIndigoUser(IamAccount entity) {

    ScimIndigoUser.Builder indigoUserBuilder = new ScimIndigoUser.Builder();

    for (IamOidcId oidcId : entity.getOidcIds()) {
      ScimOidcId scimOidcid =
          new ScimOidcId.Builder().issuer(oidcId.getIssuer()).subject(oidcId.getSubject()).build();

      indigoUserBuilder.addOidcid(scimOidcid);

    }

    ScimIndigoUser indigoUser = indigoUserBuilder.build();

    return indigoUser.isEmpty() ? null : indigoUser;
  }

  private ScimGroupRef getScimGroupRef(IamGroup group) {

    return ScimGroupRef.builder().value(group.getUuid()).display(group.getName())
        .ref(resourceLocationProvider.groupLocation(group.getUuid())).build();
  }

  private ScimAddress getScimAddress(IamAccount entity) {

    if (entity.getUserInfo() != null && entity.getUserInfo().getAddress() != null) {

      return addressConverter.toScim(entity.getUserInfo().getAddress());
    }
    return null;
  }

  private ScimX509Certificate getScimX509Certificate(IamX509Certificate cert) {

    return certificateConverter.toScim(cert);
  }
}
