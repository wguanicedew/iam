package it.infn.mw.iam.api.scim.new_updater.builders;

import org.springframework.security.crypto.password.PasswordEncoder;

import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

public abstract class BuilderSupport {

  protected final IamAccountRepository repo;
  protected final PasswordEncoder encoder;
  protected final IamAccount account;

  public BuilderSupport(IamAccountRepository repo, IamAccount account) {
    this(repo, null, account);
  }

  public BuilderSupport(IamAccountRepository repo, PasswordEncoder encoder, IamAccount account) {
    this.repo = repo;
    this.encoder = encoder;
    this.account = account;
  }

}

