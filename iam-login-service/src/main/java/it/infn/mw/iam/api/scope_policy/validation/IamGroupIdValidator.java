package it.infn.mw.iam.api.scope_policy.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import it.infn.mw.iam.persistence.repository.IamGroupRepository;

public class IamGroupIdValidator implements ConstraintValidator<IamGroupId, String>{

  final IamGroupRepository groupRepo;
  
  @Autowired
  public IamGroupIdValidator(IamGroupRepository groupRepo) {
    this.groupRepo = groupRepo;
  }

  @Override
  public void initialize(IamGroupId constraintAnnotation) {
    // empty
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return value != null && groupRepo.findByUuid(value).isPresent();
  }

}
