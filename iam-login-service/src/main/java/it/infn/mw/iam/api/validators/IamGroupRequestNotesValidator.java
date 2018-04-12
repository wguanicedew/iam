package it.infn.mw.iam.api.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

@Component
@Scope("prototype")
public class IamGroupRequestNotesValidator
    implements ConstraintValidator<IamGroupRequestNotes, String> {

  @Override
  public void initialize(IamGroupRequestNotes constraintAnnotation) {
    // empty method
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return value != null && !Strings.isNullOrEmpty(value.trim());
  }
}
