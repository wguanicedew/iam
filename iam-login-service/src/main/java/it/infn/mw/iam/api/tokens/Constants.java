package it.infn.mw.iam.api.tokens;

public class Constants {

  public static final String ACCESS_TOKENS_ENDPOINT = "/iam/api/access-tokens";
  public static final String REFRESH_TOKENS_ENDPOINT = "/iam/api/refresh-tokens";

  private Constants() {
    throw new IllegalStateException("Utility class");
  }
}
