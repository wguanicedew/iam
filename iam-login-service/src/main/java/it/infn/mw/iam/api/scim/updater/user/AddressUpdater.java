package it.infn.mw.iam.api.scim.updater.user;

import javax.persistence.EntityManager;

import org.mitre.openid.connect.model.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import it.infn.mw.iam.api.scim.converter.AddressConverter;
import it.infn.mw.iam.api.scim.exception.ScimResourceNotFoundException;
import it.infn.mw.iam.api.scim.model.ScimAddress;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.updater.Updater;
import it.infn.mw.iam.persistence.model.IamAccount;

@Component
public class AddressUpdater implements Updater<IamAccount, ScimUser> {

  @Autowired
  private AddressConverter addressConverter;
  @Autowired
  private EntityManager entityManager;

  private void validate(IamAccount account, ScimUser user) {
    
    Preconditions.checkNotNull(account);
    Preconditions.checkNotNull(user);
    Preconditions.checkNotNull(user.getAddresses());
    Preconditions.checkArgument(!user.getAddresses().isEmpty(),
        "Empty address list");
    Preconditions.checkArgument(user.getAddresses().size() == 1,
        "Specifying more than one address is not supported!");
    Preconditions.checkNotNull(user.getAddresses().get(0), "Null address found");
  }

  @Override
  public boolean add(IamAccount account, ScimUser user) {

    return replace(account, user);
  }

  @Override
  public boolean remove(IamAccount account, ScimUser user) {

    validate(account, user);

    Address currentAddress = account.getUserInfo().getAddress();
    
    if (currentAddress == null) {
      // nothing to remove
      throw new ScimResourceNotFoundException("Address not found for user");
    }

    ScimAddress address = addressConverter.toScim(account.getUserInfo().getAddress());
    if (!address.equals(user.getAddresses().get(0))) {
      throw new ScimResourceNotFoundException("Address not found for user");
    }

    Address oldAddress = account.getUserInfo().getAddress();
    account.getUserInfo().setAddress(null);
    entityManager.remove(oldAddress);

    return true;
  }

  @Override
  public boolean replace(IamAccount account, ScimUser user) {

    validate(account, user);

    if (account.getUserInfo().getAddress() != null) {
      ScimAddress address = addressConverter.toScim(account.getUserInfo().getAddress());
      if (address.equals(user.getAddresses().get(0))) {
        return false;
      }
    }

    final Address address = addressConverter.fromScim(user.getAddresses().get(0));
    entityManager.persist(address);

    account.getUserInfo().setAddress(address);
    return true;
  }

  @Override
  public boolean accept(ScimUser user) {

    return user.getAddresses() != null && !user.getAddresses().isEmpty();
  }

}
