package it.infn.mw.iam.test.oauth.assertions;

import static java.util.Collections.singletonList;

import java.time.Instant;
import java.util.Date;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import it.infn.mw.iam.test.oauth.EndpointsTestUtils;

public class JWTBearerClientAuthenticationTestSupport extends EndpointsTestUtils {

  public static final String CLIENT_ID_SECRET_JWT = "jwt-auth-client_secret_jwt";
  public static final String CLIENT_ID_SECRET_JWT_SECRET = "c8e9eed0-e6e4-4a66-b16e-6f37096356a7";
  public static final String TOKEN_ENDPOINT_AUDIENCE = "http://localhost:8080/token";
  public static final String TOKEN_ENDPOINT = "/token";
  public static final String JWT_BEARER_ASSERTION_TYPE =
      "urn:ietf:params:oauth:client-assertion-type:jwt-bearer";

  public SignedJWT createClientAuthToken(String clientId, Instant expirationTime)
      throws JOSEException {

    JWSSigner signer = new MACSigner(CLIENT_ID_SECRET_JWT_SECRET);
    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject(clientId)
      .issuer(clientId)
      .expirationTime(Date.from(expirationTime))
      .audience(singletonList(TOKEN_ENDPOINT_AUDIENCE))
      .build();

    SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

    signedJWT.sign(signer);

    return signedJWT;
  }

}
