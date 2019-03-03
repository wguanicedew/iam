package it.infn.mw.iam.test.registration.cern;

import static it.infn.mw.iam.util.BasicAuthenticationUtils.basicAuthHeaderValue;

public class CernTestSupport {

  public static final String HR_API_USERNAME = "user";
  public static final String HR_API_PASSWORD = "password";
  public static final String HR_API_URL = "https://hr.cern.ch";

  public static final String SSO_ENTITY_ID = "https://cern.ch/login";
  public static final String EXPERIMENT_NAME = "wlcg";

  public static final String BASIC_AUTH_HEADER_VALUE =
      basicAuthHeaderValue(HR_API_USERNAME, HR_API_PASSWORD);

  public static final String API_VALIDATION_URL =
      String.format("%s/api/VOPersons/participation/wlcg/valid", HR_API_URL);

  public static String apiValidationUrl(String personId) {
    return String.format("%s/%s", API_VALIDATION_URL, personId);
  }
}
