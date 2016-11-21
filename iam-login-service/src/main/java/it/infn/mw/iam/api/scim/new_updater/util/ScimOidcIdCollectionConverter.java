package it.infn.mw.iam.api.scim.new_updater.util;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import it.infn.mw.iam.api.scim.converter.OidcIdConverter;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.persistence.model.IamOidcId;

public class ScimOidcIdCollectionConverter implements Supplier<Collection<IamOidcId>> {

  final ScimUser user;
  final OidcIdConverter converter;

  public ScimOidcIdCollectionConverter(OidcIdConverter converter, ScimUser user) {
    this.converter = converter;
    this.user = user;
  }

  @Override
  public Collection<IamOidcId> get() {

    if (!user.hasOidcIds()) {
      return null;
    }

    return user.getIndigoUser()
      .getOidcIds()
      .stream()
      .filter(Objects::nonNull)
      .map(i -> converter.fromScim(i))
      .collect(Collectors.toList());
  }
}
