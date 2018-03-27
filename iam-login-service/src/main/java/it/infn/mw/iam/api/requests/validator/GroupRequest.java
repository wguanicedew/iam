package it.infn.mw.iam.api.requests.validator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Retention(RUNTIME)
@Target({TYPE, ANNOTATION_TYPE})
@Constraint(validatedBy = GroupRequestValidator.class)
public @interface GroupRequest {
  String message() default "Invalid group membership request: group and notes cannot be empty";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
