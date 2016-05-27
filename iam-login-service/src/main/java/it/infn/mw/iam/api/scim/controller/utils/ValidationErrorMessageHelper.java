package it.infn.mw.iam.api.scim.controller.utils;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public class ValidationErrorMessageHelper {

  private ValidationErrorMessageHelper() {
  }

  public static String buildValidationErrorMessage(String errorMessage,
    BindingResult validationResult) {

    StringBuilder validationError = new StringBuilder();
    validationError.append(errorMessage + ": ");

    boolean first = true;

    for (ObjectError error : validationResult.getAllErrors()) {

      if (!first) {
        validationError.append(",");
      }

      if (error instanceof FieldError) {
        FieldError fieldError = (FieldError) error;

        validationError
          .append(String.format("[%s.%s : %s]", fieldError.getObjectName(),
            fieldError.getField(), fieldError.getDefaultMessage()));

      } else {

        validationError.append(String.format("[%s : %s]", error.getObjectName(),
          error.getDefaultMessage()));
      }

      first = false;
    }

    return validationError.toString();
  }

}
