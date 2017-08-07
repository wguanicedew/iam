package it.infn.mw.iam.core.user;

import java.util.Date;
import java.util.List;

import it.infn.mw.iam.persistence.model.IamAccount;

/**
 * This service provides basic functionality used to manage IAM accounts
 */
public interface IamAccountService {

  /**
   * Creates a new {@link IamAccount}, after some checks.
   * 
   * @param account the account to be created
   * @return the created {@link IamAccount}
   */
  IamAccount createAccount(IamAccount account);


  /**
   * Deletes a {@link IamAccount}.
   * 
   * @param account the account to be deleted
   * 
   * @return the deleted {@link IamAccount}
   */
  IamAccount deleteAccount(IamAccount account);

  /**
   * Deletes provisioned accounts whose last login time is before than the timestamp passed as
   * argument
   * 
   * @param timestamp the timestamp
   * @return the possibly empty {@link List} of {@link IamAccount} that have been removed
   */
  List<IamAccount> deleteInactiveProvisionedUsersSinceTime(Date timestamp);
}
