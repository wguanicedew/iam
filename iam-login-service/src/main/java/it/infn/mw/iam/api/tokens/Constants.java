package it.infn.mw.iam.api.tokens;

public class Constants {

  private Constants() {
    throw new IllegalStateException("Utility class");
  }

  public static final String ACCESS_TOKENS_ENDPOINT = "/iam/api/access-tokens";
  public static final String REFRESH_TOKENS_ENDPOINT = "/iam/api/refresh-tokens";
}
