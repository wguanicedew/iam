package it.infn.mw.iam.api.scim.new_updater;

import org.springframework.security.crypto.password.PasswordEncoder;

import it.infn.mw.iam.api.scim.new_updater.builders.Adders;
import it.infn.mw.iam.api.scim.new_updater.builders.Removers;
import it.infn.mw.iam.api.scim.new_updater.builders.Replacers;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

public class Updaters {

  public static Adders adders(IamAccountRepository repo, PasswordEncoder encoder,
      IamAccount account) {
    return new Adders(repo, encoder, account);
  }

  public static Removers removers(IamAccountRepository repo, IamAccount account) {
    return new Removers(repo, account);
  }

  public static Replacers replacers(IamAccountRepository repo, PasswordEncoder encoder,
      IamAccount account) {
    return new Replacers(repo, encoder, account);
  }

}
