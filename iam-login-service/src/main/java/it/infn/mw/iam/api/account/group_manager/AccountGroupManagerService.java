package it.infn.mw.iam.api.account.group_manager;

import java.util.List;

import it.infn.mw.iam.api.account.group_manager.model.AccountManagedGroupsDTO;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamGroup;

public interface AccountGroupManagerService {

  AccountManagedGroupsDTO getManagedGroupInfoForAccount(IamAccount account);
  
  void addManagedGroupForAccount(IamAccount account, IamGroup group);
  void removeManagedGroupForAccount(IamAccount account, IamGroup group);
  
  List<IamAccount> getGroupManagersForGroup(IamGroup group);
  
}
