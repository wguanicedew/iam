package it.infn.mw.iam.api.common;

public class ErrorDTO {

  private final String error;

  private ErrorDTO(String error) {
    this.error = error;
  }

  public String getError() {
    return error;
  }

  public static ErrorDTO fromString(String error) {
    return new ErrorDTO(error);
  }
}
