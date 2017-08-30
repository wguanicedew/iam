package it.infn.mw.iam.api.scope_policy.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import it.infn.mw.iam.api.scope_policy.ScopePolicyDTO;

public class ScopePolicyValidator implements ConstraintValidator<ScopePolicy, ScopePolicyDTO> {

  public ScopePolicyValidator() {
    // empty
  }

  @Override
  public void initialize(ScopePolicy constraintAnnotation) {
    // empty
  }

  @Override
  public boolean isValid(ScopePolicyDTO value, ConstraintValidatorContext context) {

    if (value == null || (value.getAccount() != null && value.getGroup() != null)) {
      return false;
    }

    return true;
  }

}
