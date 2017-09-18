package it.infn.mw.iam.authn.saml.util.metadata;

public class ValidationResult {

  private final boolean valid;
  private final String message;

  private ValidationResult(boolean valid, String message) {
    this.valid = valid;
    this.message = message;
  }

  public boolean isValid() {
    return valid;
  }
  
  public String getMessage() {
    return message;
  }

  public static ValidationResult valid() {
    return new ValidationResult(true, null);
  }

  public static ValidationResult invalid(String message) {
    return new ValidationResult(false, message);
  }
}
