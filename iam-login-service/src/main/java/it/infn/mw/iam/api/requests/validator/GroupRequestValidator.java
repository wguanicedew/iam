package it.infn.mw.iam.api.requests.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.google.common.base.Strings;

import it.infn.mw.iam.api.requests.model.GroupRequestDto;

public class GroupRequestValidator implements ConstraintValidator<GroupRequest, GroupRequestDto> {

  public GroupRequestValidator() {
    // empty
  }

  @Override
  public void initialize(GroupRequest constraintAnnotation) {
    // empty
  }

  @Override
  public boolean isValid(GroupRequestDto value, ConstraintValidatorContext context) {

    return value != null && !Strings.isNullOrEmpty(value.getGroupName())
        && !Strings.isNullOrEmpty(value.getNotes());
  }

}
