package it.infn.mw.iam.api.aup;

public class AupAlreadyExistsError extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  public static final String MESSAGE = "AUP already exists";
  
  public AupAlreadyExistsError() {
    super(MESSAGE);
  }

}
