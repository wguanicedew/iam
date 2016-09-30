package it.infn.mw.iam.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * A simple util to quickly get a password bcrypt-encoded
 *
 */
public class IamBcryptUtil {

  public static void main(String[] args) {

    if (args.length == 0) {
      System.err.println("Please provide the password to encode as an argument");
      System.exit(1);
    }

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    System.out.println(encoder.encode(args[0]));
  }

}
