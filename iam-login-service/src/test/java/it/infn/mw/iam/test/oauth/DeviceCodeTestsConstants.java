package it.infn.mw.iam.test.oauth;

public interface DeviceCodeTestsConstants {

  public static final String DEVICE_CODE_ENDPOINT = "/devicecode";
  public static final String DEVICE_CODE_USER_ENDPOINT = "/device";
  public static final String TOKEN_ENDPOINT = "/token";
  public static final String USERINFO_ENDPOINT = "/userinfo";
  public static final String INTROSPECTION_ENDPOINT = "/introspect";

  public static final String PUBLIC_DEVICE_CODE_CLIENT_ID = "public-dc-client";

  public static final String DEVICE_CODE_CLIENT_ID = "device-code-client";
  public static final String DEVICE_CODE_CLIENT_SECRET = "secret";
  public static final String DEVICE_CODE_GRANT_TYPE =
      "urn:ietf:params:oauth:grant-type:device_code";

  public static final String DEVICE_USER_URL = "http://localhost:8080/device";
  public static final String DEVICE_USER_VERIFY_URL = "http://localhost:8080/device/verify";
  public static final String DEVICE_USER_APPROVE_URL = "http://localhost:8080/device/approve";

  public static final String LOGIN_URL = "/login";
  public static final String TEST_USERNAME = "test";
  public static final String TEST_PASSWORD = "password";

}
