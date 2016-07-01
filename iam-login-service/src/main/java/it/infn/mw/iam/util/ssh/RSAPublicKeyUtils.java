package it.infn.mw.iam.util.ssh;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class RSAPublicKeyUtils {

  public static String getMD5Fingerprint(String key) {

    return buildMD5Fingerprint(key);
  }

  public static String getFormattedMD5Fingerprint(String key) {

    return String.join(":", buildMD5Fingerprint(key).split("(?<=\\G..)"));
  }

  public static String getSHA256Fingerprint(String key) {

    return buildSHA256Fingerprint(key);
  }

  private static String buildMD5Fingerprint(String key) throws InvalidSshKeyException {

    String fingerprint = null;

    try {

      byte[] decodedKey = Base64.getDecoder().decode(key);
      byte[] digest = MessageDigest.getInstance(MessageDigestAlgorithms.MD5).digest(decodedKey);
      fingerprint = Hex.encodeHexString(digest);

    } catch (NoSuchAlgorithmException e) {

      /* Algorithm is MessageDigestAlgorithms.MD5 so the exception is never thrown */
      assert false;

    } catch (IllegalArgumentException iae) {

      throw new InvalidSshKeyException(
          "Error during fingerprint generation: RSA key is not base64 encoded", iae);
    }

    return fingerprint;
  }

  private static String buildSHA256Fingerprint(String key) throws InvalidSshKeyException {

    String fingerprint = null;

    try {

      byte[] decodedKey = Base64.getDecoder().decode(key);
      byte[] digest = MessageDigest.getInstance(MessageDigestAlgorithms.SHA_256).digest(decodedKey);
      fingerprint = Base64.getEncoder().encodeToString(digest);

    } catch (NoSuchAlgorithmException e) {

      /* Algorithm is MessageDigestAlgorithms.SHA_256 so the exception is never thrown */
      assert false;

    } catch (IllegalArgumentException iae) {

      throw new InvalidSshKeyException(
          "Error during fingerprint generation: RSA key is not base64 encoded", iae);
    }

    return fingerprint;
  }
}
