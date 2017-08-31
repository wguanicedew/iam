package it.infn.mw.iam.registration;
@FunctionalInterface
public interface TokenGenerator {

  String generateToken();

}
