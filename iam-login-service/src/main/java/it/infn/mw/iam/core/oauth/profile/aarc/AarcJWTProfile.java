package it.infn.mw.iam.core.oauth.profile.aarc;

import org.springframework.security.oauth2.provider.OAuth2Request;

import it.infn.mw.iam.core.oauth.profile.IDTokenCustomizer;
import it.infn.mw.iam.core.oauth.profile.IntrospectionResultHelper;
import it.infn.mw.iam.core.oauth.profile.JWTAccessTokenBuilder;
import it.infn.mw.iam.core.oauth.profile.JWTProfile;
import it.infn.mw.iam.core.oauth.profile.RequestValidator;
import it.infn.mw.iam.core.oauth.profile.UserInfoHelper;

public class AarcJWTProfile implements JWTProfile, RequestValidator {

  public static final String PROFILE_NAME = "AARC JWT profile";

  private final JWTAccessTokenBuilder accessTokenBuilder;
  private final IDTokenCustomizer idTokenCustomizer;
  private final UserInfoHelper userInfoHelper;
  private final IntrospectionResultHelper introspectionHelper;

  public AarcJWTProfile(JWTAccessTokenBuilder accessTokenBuilder, IDTokenCustomizer idTokenBuilder,
      UserInfoHelper userInfoHelper, IntrospectionResultHelper introspectionHelper) {
    this.accessTokenBuilder = accessTokenBuilder;
    this.idTokenCustomizer = idTokenBuilder;
    this.userInfoHelper = userInfoHelper;
    this.introspectionHelper = introspectionHelper;
  }

  @Override
  public void validateRequest(OAuth2Request request) {
    // no validation
  }

  @Override
  public String name() {
    return PROFILE_NAME;
  }

  @Override
  public JWTAccessTokenBuilder getAccessTokenBuilder() {
    return accessTokenBuilder;
  }

  @Override
  public IDTokenCustomizer getIDTokenCustomizer() {
    return idTokenCustomizer;
  }

  @Override
  public IntrospectionResultHelper getIntrospectionResultHelper() {
    return introspectionHelper;
  }

  @Override
  public UserInfoHelper getUserinfoHelper() {
    return userInfoHelper;
  }

  @Override
  public RequestValidator getRequestValidator() {
    return this;
  }

}
