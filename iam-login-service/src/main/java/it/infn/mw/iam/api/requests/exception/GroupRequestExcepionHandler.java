package it.infn.mw.iam.api.requests.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GroupRequestExcepionHandler extends ResponseEntityExceptionHandler {

  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(GroupRequestValidationException.class)
  @ResponseBody
  public ErrorResponse handleValidationException(GroupRequestValidationException e) {

    return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
  }

  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(GroupRequestStatusException.class)
  @ResponseBody
  public ErrorResponse handleValidationException(GroupRequestStatusException e) {

    return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
  }

  @ResponseStatus(code = HttpStatus.FORBIDDEN)
  @ExceptionHandler(UserMismatchException.class)
  @ResponseBody
  public ErrorResponse handleValidationException(UserMismatchException e) {

    return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
  }

  private ErrorResponse buildErrorResponse(HttpStatus status, String message) {
    return new ErrorResponse(status.value(), status.getReasonPhrase(), message);
  }

}
