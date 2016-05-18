package it.infn.mw.iam.api.scim.converter;

import org.mitre.openid.connect.model.Address;
import org.springframework.stereotype.Service;

import it.infn.mw.iam.api.scim.model.ScimAddress;

@Service
public class AddressConverter implements Converter<ScimAddress, Address> {

  @Override
  public Address fromScim(ScimAddress scim) {

    return null;
  }

  @Override
  public ScimAddress toScim(Address entity) {

    // TODO Auto-generated method stub
    return null;
  }

}
