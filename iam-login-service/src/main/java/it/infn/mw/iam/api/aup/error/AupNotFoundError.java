package it.infn.mw.iam.api.aup.error;

public class AupNotFoundError extends RuntimeException{

  public static final String AUP_NOT_DEFINED = "AUP is not defined for this organization";
  private static final long serialVersionUID = 1L;

  public AupNotFoundError() {
    super(AUP_NOT_DEFINED);
  }

}
