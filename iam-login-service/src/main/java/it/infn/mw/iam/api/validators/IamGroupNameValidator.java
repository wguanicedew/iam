package it.infn.mw.iam.api.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import it.infn.mw.iam.persistence.repository.IamGroupRepository;

@Component
@Scope("prototype")
public class IamGroupNameValidator implements ConstraintValidator<IamGroupName, String> {

  @Autowired
  private IamGroupRepository groupRepository;

  @Override
  public void initialize(IamGroupName constraintAnnotation) {
    // empty method
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return value != null && groupRepository.findByName(value).isPresent();
  }
}
