package it.infn.mw.iam.test.oauth;

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mitre.openid.connect.model.UserInfo;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import it.infn.mw.iam.core.userinfo.DelegateUserInfoAdapter;
import it.infn.mw.iam.persistence.model.IamAddress;
import it.infn.mw.iam.persistence.repository.AddressAdapter;

@RunWith(MockitoJUnitRunner.class)
public class DelegateUserInfoTests {

  @Mock
  UserInfo delegate;

  @Mock
  IamAddress addressDelegate;
  
  @InjectMocks
  DelegateUserInfoAdapter adapter;
  
  @InjectMocks
  AddressAdapter addressAdapter;


  @Test
  public void testDelegateCalls() {

    // this is mostly due to the adapter destroying our coverage
    // statistics in the core package
    adapter.getAddress();
    adapter.getBirthdate();
    adapter.getEmail();
    adapter.getEmailVerified();
    adapter.getFamilyName();
    adapter.getGender();
    adapter.getGivenName();
    adapter.getLocale();
    adapter.getMiddleName();
    adapter.getName();
    adapter.getNickname();
    adapter.getPhoneNumber();
    adapter.getPhoneNumberVerified();
    adapter.getPicture();
    adapter.getPreferredUsername();
    adapter.getProfile();
    adapter.getSource();
    adapter.getSub();
    adapter.getUpdatedTime();
    adapter.getWebsite();
    adapter.getZoneinfo();

    verify(delegate).getAddress();
    verify(delegate).getBirthdate();
    verify(delegate).getEmail();
    verify(delegate).getEmailVerified();
    verify(delegate).getFamilyName();
    verify(delegate).getGender();
    verify(delegate).getGivenName();
    verify(delegate).getLocale();
    verify(delegate).getMiddleName();
    verify(delegate).getName();
    verify(delegate).getNickname();
    verify(delegate).getPhoneNumber();
    verify(delegate).getPhoneNumberVerified();
    verify(delegate).getPicture();
    verify(delegate).getPreferredUsername();
    verify(delegate).getProfile();
    verify(delegate).getSource();
    verify(delegate).getSub();
    verify(delegate).getUpdatedTime();
    verify(delegate).getWebsite();
    verify(delegate).getZoneinfo();

    adapter.setAddress(null);
    adapter.setBirthdate(null);
    adapter.setEmail(null);
    adapter.setEmailVerified(null);
    adapter.setFamilyName(null);
    adapter.setGender(null);
    adapter.setGivenName(null);
    adapter.setLocale(null);
    adapter.setMiddleName(null);
    adapter.setName(null);
    adapter.setNickname(null);
    adapter.setPhoneNumber(null);
    adapter.setPhoneNumberVerified(null);
    adapter.setPicture(null);
    adapter.setPreferredUsername(null);
    adapter.setProfile(null);
    adapter.setSub(null);
    adapter.setUpdatedTime(null);
    adapter.setWebsite(null);
    adapter.setZoneinfo(null);

    verify(delegate).setAddress(null);
    verify(delegate).setBirthdate(null);
    verify(delegate).setEmail(null);
    verify(delegate).setEmailVerified(null);
    verify(delegate).setFamilyName(null);
    verify(delegate).setGender(null);
    verify(delegate).setGivenName(null);
    verify(delegate).setLocale(null);
    verify(delegate).setMiddleName(null);
    verify(delegate).setName(null);
    verify(delegate).setNickname(null);
    verify(delegate).setPhoneNumber(null);
    verify(delegate).setPhoneNumberVerified(null);
    verify(delegate).setPicture(null);
    verify(delegate).setPreferredUsername(null);
    verify(delegate).setProfile(null);
    verify(delegate).setSub(null);
    verify(delegate).setUpdatedTime(null);
    verify(delegate).setWebsite(null);
    verify(delegate).setZoneinfo(null);

    adapter.toJson();
    verify(delegate).toJson();
    
    addressAdapter.getId();
    addressAdapter.getCountry();
    addressAdapter.getFormatted();
    addressAdapter.getLocality();
    addressAdapter.getPostalCode();
    addressAdapter.getRegion();
    addressAdapter.getStreetAddress();
    
    verify(addressDelegate).getId();
    verify(addressDelegate).getCountry();
    verify(addressDelegate).getFormatted();
    verify(addressDelegate).getLocality();
    verify(addressDelegate).getPostalCode();
    verify(addressDelegate).getRegion();
    verify(addressDelegate).getStreetAddress();
    
    addressAdapter.setCountry(null);
    
    addressAdapter.setFormatted(null);
    addressAdapter.setLocality(null);
    addressAdapter.setPostalCode(null);
    addressAdapter.setRegion(null);
    addressAdapter.setStreetAddress(null);
    
    verify(addressDelegate).setFormatted(null);
    verify(addressDelegate).setLocality(null);
    verify(addressDelegate).setPostalCode(null);
    verify(addressDelegate).setRegion(null);
    verify(addressDelegate).setStreetAddress(null);
  }
}
