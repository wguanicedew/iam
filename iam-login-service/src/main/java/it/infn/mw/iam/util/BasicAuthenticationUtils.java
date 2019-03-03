package it.infn.mw.iam.util;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.US_ASCII;

import org.apache.commons.codec.binary.Base64;

public class BasicAuthenticationUtils {

  private BasicAuthenticationUtils() {
    // prevent instantiation
  }

  public static final String basicAuthHeaderValue(String username, String password) {
    StringBuilder builder = new StringBuilder();
    String auth = format("%s:%s", username, password);
    String encodedAuth = Base64.encodeBase64String(auth.getBytes(US_ASCII));
    
    builder.append("Basic ");
    builder.append(encodedAuth);
    return builder.toString();
  }
}
