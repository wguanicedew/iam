package it.infn.mw.iam.api.tokens.exception;

public class TokenNotFoundException extends RuntimeException {

  private final Long tokenId;

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public TokenNotFoundException(Long tokenId) {
    super("Token with id = " + tokenId + " not found");
    this.tokenId = tokenId;
  }

  public Long getTokenId() {
    return tokenId;
  }

}
