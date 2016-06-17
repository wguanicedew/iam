package it.infn.mw.tc;

import org.mitre.openid.connect.model.OIDCAuthenticationToken;

public class OpenIDAuthentication {

  String issuer;
  String sub;

  String name;
  String familyName;

  String accessToken;
  String refreshToken;
  String idToken;

  String userInfo;

  public OpenIDAuthentication(OIDCAuthenticationToken token) {
    issuer = token.getIssuer();
    sub = token.getSub();

    accessToken = token.getAccessTokenValue();
    refreshToken = token.getRefreshTokenValue();
    idToken = token.getIdToken().getParsedString();
    

    name = token.getUserInfo().getName();
    familyName = token.getUserInfo().getFamilyName();
    userInfo = token.getUserInfo().toJson().toString();

  }

  public String getIssuer() {

    return issuer;
  }

  public String getSub() {

    return sub;
  }

  public String getAccessToken() {

    return accessToken;
  }

  public String getRefreshToken() {

    return refreshToken;
  }

  public String getIdToken() {

    return idToken;
  }

  public String getUserInfo() {

    return userInfo;
  }

  public String getName() {

    return name;
  }

  public String getFamilyName() {

    return familyName;
  }

}
