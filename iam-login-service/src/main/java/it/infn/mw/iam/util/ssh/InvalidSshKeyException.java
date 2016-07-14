package it.infn.mw.iam.util.ssh;

public class InvalidSshKeyException extends RuntimeException {

  public InvalidSshKeyException(String message, Throwable cause) {
    
    super(message, cause);
  }

  private static final long serialVersionUID = 1L;

}
