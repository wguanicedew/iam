package it.infn.mw.iam.api.scope_policy.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Retention(RUNTIME)
@Target({TYPE, ANNOTATION_TYPE})
@Constraint(validatedBy=ScopePolicyValidator.class)
public @interface ScopePolicy {
  String message() default "Invalid scope policy: group and account cannot be both non-null";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
