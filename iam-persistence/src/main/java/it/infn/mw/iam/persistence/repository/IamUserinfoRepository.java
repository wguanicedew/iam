package it.infn.mw.iam.persistence.repository;

import java.util.Optional;

import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;

import it.infn.mw.iam.persistence.model.IamAccount;


public class IamUserinfoRepository implements UserInfoRepository {

  @Autowired
  private IamAccountRepository repo;

  @Override
  public UserInfo getByUsername(String username) {

    Optional<IamAccount> account = repo.findByUsername(username);
    
    if (account.isPresent()){
      return account.get().getUserInfo();
    }
    
    return null;
  }

  @Override
  public UserInfo getByEmailAddress(String email) {
    
    Optional<IamAccount> account = repo.findByEmail(email);
    
    if (account.isPresent()){
      return account.get().getUserInfo();
    }
    
    return null;
  }

}
