package it.infn.mw.iam.api.scim.controller.utils;

import org.springframework.validation.BindingResult;

import it.infn.mw.iam.api.scim.exception.ScimValidationException;

public class ValidationHelper {

  public static void handleValidationError(final String message,
    final BindingResult validationResult) {

    if (validationResult.hasErrors()) {
      throw new ScimValidationException(ValidationErrorMessageHelper
        .buildValidationErrorMessage(message, validationResult));
    }
  }

}
