package it.infn.mw.iam.registration;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class UUIDTokenGenerator implements TokenGenerator {

  @Override
  public String generateToken() {

    return UUID.randomUUID()
      .toString();
  }

}
