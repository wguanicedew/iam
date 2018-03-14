package it.infn.mw.iam.api.requests.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import it.infn.mw.iam.api.common.ErrorDTO;

@ControllerAdvice
public class GroupRequestExceptionHandler extends ResponseEntityExceptionHandler {

  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(GroupRequestValidationException.class)
  @ResponseBody
  public ErrorDTO handleValidationException(GroupRequestValidationException e) {

    return buildErrorResponse(e.getMessage());
  }

  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(GroupRequestStatusException.class)
  @ResponseBody
  public ErrorDTO handleValidationException(GroupRequestStatusException e) {

    return buildErrorResponse(e.getMessage());
  }

  @ResponseStatus(code = HttpStatus.FORBIDDEN)
  @ExceptionHandler(UserMismatchException.class)
  @ResponseBody
  public ErrorDTO handleValidationException(UserMismatchException e) {

    return buildErrorResponse(e.getMessage());
  }

  private ErrorDTO buildErrorResponse(String message) {
    return ErrorDTO.fromString(message);
  }

}
