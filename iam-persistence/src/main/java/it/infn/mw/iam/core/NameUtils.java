package it.infn.mw.iam.core;


public class NameUtils {

  public static String getFormatted(String givenName, String middleName, String familyName) {

    StringBuilder builder = new StringBuilder();
    builder.append(givenName);

    if (middleName != null) {
      builder.append(" ");
      builder.append(middleName);
    }

    builder.append(" ");
    builder.append(familyName);
    return builder.toString();
  }

}
