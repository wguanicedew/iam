package it.infn.mw.iam.api.scim.updater.user;

import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import it.infn.mw.iam.api.scim.exception.ScimResourceNotFoundException;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.updater.Updater;
import it.infn.mw.iam.persistence.model.IamAccount;

@Component
public class PhotoUpdater implements Updater<IamAccount, ScimUser> {

  private void validate(IamAccount account, ScimUser user) {

    Preconditions.checkNotNull(account);
    Preconditions.checkNotNull(user);
    Preconditions.checkNotNull(user.getPhotos());
    Preconditions.checkArgument(!user.getPhotos().isEmpty());
    Preconditions.checkArgument(user.getPhotos().size() == 1);
    Preconditions.checkNotNull(user.getPhotos().get(0), "Null photo found");
    Preconditions.checkNotNull(user.getPhotos().get(0).getValue(), "Null photo value found");
  }

  @Override
  public boolean add(IamAccount account, ScimUser user) {

    validate(account, user);

    final String picture = user.getPhotos().get(0).getValue();

    if (picture.equals(account.getUserInfo().getPicture())) {
      return false;
    }

    account.getUserInfo().setPicture(picture);
    return true;
  }

  @Override
  public boolean remove(IamAccount account, ScimUser user) {

    validate(account, user);

    final String picture = user.getPhotos().get(0).getValue();

    if (!picture.equals(account.getUserInfo().getPicture())) {
      throw new ScimResourceNotFoundException("Photo value " + picture + " not found");
    }
    account.getUserInfo().setPicture(null);
    return true;
  }

  @Override
  public boolean replace(IamAccount account, ScimUser user) {

    validate(account, user);

    final String picture = user.getPhotos().get(0).getValue();

    if (picture.equals(account.getUserInfo().getPicture())) {
      return false;
    }
    account.getUserInfo().setPicture(picture);
    return true;
  }

  @Override
  public boolean accept(ScimUser user) {

    return user.getPhotos() != null && !user.getPhotos().isEmpty();
  }

}
