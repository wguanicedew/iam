package it.infn.mw.iam.api.scim.new_updater.util;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import it.infn.mw.iam.api.scim.converter.SamlIdConverter;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.persistence.model.IamSamlId;

public class ScimSamlIdCollectionConverter implements Supplier<Collection<IamSamlId>> {

  final ScimUser user;
  final SamlIdConverter converter;

  public ScimSamlIdCollectionConverter(SamlIdConverter converter, ScimUser user) {
    this.converter = converter;
    this.user = user;

  }

  @Override
  public Collection<IamSamlId> get() {

    if (!user.hasSamlIds()) {
      return null;
    }

    return user.getIndigoUser()
      .getSamlIds()
      .stream()
      .filter(Objects::nonNull)
      .map(i -> converter.fromScim(i))
      .collect(Collectors.toList());

  }

}
