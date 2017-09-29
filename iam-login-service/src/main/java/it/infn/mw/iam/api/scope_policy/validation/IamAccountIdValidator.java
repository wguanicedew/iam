package it.infn.mw.iam.api.scope_policy.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import it.infn.mw.iam.persistence.repository.IamAccountRepository;

public class IamAccountIdValidator implements ConstraintValidator<IamAccountId, String>{

  private final IamAccountRepository accountRepo;
  
  @Autowired
  public IamAccountIdValidator(IamAccountRepository accountRepo) {
    
    this.accountRepo = accountRepo;
  }

  @Override
  public void initialize(IamAccountId constraintAnnotation) {
    // empty 
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return value != null && accountRepo.findByUuid(value).isPresent();
  }

}
