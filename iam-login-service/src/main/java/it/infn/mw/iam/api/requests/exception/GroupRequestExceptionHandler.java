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
  @ExceptionHandler(GroupRequestValidationError.class)
  @ResponseBody
  public ErrorDTO handleValidationException(GroupRequestValidationError e) {

    return buildErrorResponse(e.getMessage());
  }

  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(InvalidGroupRequestStatusError.class)
  @ResponseBody
  public ErrorDTO handleValidationException(InvalidGroupRequestStatusError e) {

    return buildErrorResponse(e.getMessage());
  }

  private ErrorDTO buildErrorResponse(String message) {
    return ErrorDTO.fromString(message);
  }
}
