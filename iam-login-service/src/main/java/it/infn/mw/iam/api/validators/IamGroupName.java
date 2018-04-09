package it.infn.mw.iam.api.validators;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Retention(RUNTIME)
@Target({FIELD, METHOD})
@Constraint(validatedBy = IamGroupNameValidator.class)
public @interface IamGroupName {
  String message() default "Invalid IAM group name";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
