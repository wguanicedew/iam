package it.infn.mw.iam.core;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.ParseException;

import org.mitre.oauth2.model.AuthorizationCodeEntity;
import org.mitre.oauth2.service.impl.DefaultOAuth2AuthorizationCodeService;
import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.saml.SAMLAuthenticationToken;

import com.nimbusds.jwt.JWTClaimsSet;

import it.infn.mw.iam.authn.ExternalAuthenticationToken;
import it.infn.mw.iam.persistence.model.IamExternalAuthenticationDetails;
import it.infn.mw.iam.persistence.repository.IamExternalAuthenticationDetailsRepository;

public class IamOAuth2AuthorizationCodeService extends DefaultOAuth2AuthorizationCodeService {

  public static final Logger LOG = LoggerFactory.getLogger(IamOAuth2AuthorizationCodeService.class);

  @Autowired
  IamExternalAuthenticationDetailsRepository extAuthnRepo;

  private byte[] getTokenBytes(Authentication token) throws IOException {

    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(baos)) {

      out.writeObject(token);
      return baos.toByteArray();
    }

  }

  private <T> T getTokenObject(byte[] tokenBytes) throws IOException, ClassNotFoundException {
    try (ByteArrayInputStream bais = new ByteArrayInputStream(tokenBytes);
        ObjectInput in = new ObjectInputStream(bais)) {

      return (T) in.readObject();
    }

  }

  private void linkOidcAuthenticationDetails(IamExternalAuthenticationDetails details,
      OIDCAuthenticationToken token) {

    details.setType("oidc");

    try {

      details.setAuthenticationToken(getTokenBytes(token));

    } catch (IOException ex) {
      LOG.error("Error serializing authentication token: {}", ex.getMessage(), ex);
      throw new RuntimeException(ex);
    }


    details.addAttribute("iss", token.getIssuer());
    details.addAttribute("sub", token.getSub());

    try {
      JWTClaimsSet claims = token.getIdToken().getJWTClaimsSet();

      details.setAuthenticationTime(claims.getIssueTime());
      details.setExpirationTime(claims.getExpirationTime());


    } catch (ParseException e) {
      LOG.warn("Could not parse JWT token claim set for token {}", token);
    }

  }

  private void linkSamlAuthenticationDetails(IamExternalAuthenticationDetails details,
      SAMLAuthenticationToken token) {

    // TODO
  }

  private void linkExternalAuthenticationDetails(String code, ExternalAuthenticationToken token) {

    checkNotNull(code, "Code cannot be null right after creation");

    AuthorizationCodeEntity ace = getRepository().getByCode(code);

    checkNotNull(ace, "AuthorizationCodeEntity cannot be null right after code creation");
    checkNotNull(ace.getAuthenticationHolder(),
        "AuthenticationHolder cannot be null right after code creation");

    IamExternalAuthenticationDetails details = new IamExternalAuthenticationDetails();

    details.setHolder(ace.getAuthenticationHolder());

    if (token.getExternalAuthentication() instanceof OIDCAuthenticationToken) {
      linkOidcAuthenticationDetails(details,
          (OIDCAuthenticationToken) token.getExternalAuthentication());
    } else if (token.getExternalAuthentication() instanceof SAMLAuthenticationToken) {
      linkSamlAuthenticationDetails(details,
          (SAMLAuthenticationToken) token.getExternalAuthentication());
    } else {
      throw new IllegalStateException("Unsupported external authentication type.");
    }

    extAuthnRepo.save(details);
  }

  private boolean isExternalAuthenticationToken(Authentication authn) {
    return (authn != null && authn instanceof ExternalAuthenticationToken);
  }

  @Override
  public String createAuthorizationCode(OAuth2Authentication authentication) {

    String code = super.createAuthorizationCode(authentication);

    if (isExternalAuthenticationToken(authentication.getUserAuthentication())) {

      linkExternalAuthenticationDetails(code,
          (ExternalAuthenticationToken) authentication.getUserAuthentication());
    }

    return code;
  }

  @Override
  public OAuth2Authentication consumeAuthorizationCode(String code) throws InvalidGrantException {
    AuthorizationCodeEntity result = getRepository().getByCode(code);

    if (result == null) {
      throw new InvalidGrantException("No authorization code found for value " + code);
    }

    OAuth2Authentication auth = result.getAuthenticationHolder().getAuthentication();

    IamExternalAuthenticationDetails details =
        extAuthnRepo.findByHolder(result.getAuthenticationHolder()).orElse(null);

    if (details != null) {

      try {
        Authentication externalAuthToken = getTokenObject(details.getAuthenticationToken());

        auth = new OAuth2Authentication(auth.getOAuth2Request(), externalAuthToken);

        extAuthnRepo.delete(details);

      } catch (ClassNotFoundException | IOException e) {
        throw new IllegalStateException("Error deserializing external authentication token", e);
      }
    }

    getRepository().remove(result);

    return auth;
  }

}
