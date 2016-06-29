package it.infn.mw.iam.util.ssh;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;

public class RSAPublicKey {

  private final String key;
  private HashMap<String, String> fingerprints = new HashMap<String, String>();

  public RSAPublicKey(String key) throws InvalidSshKeyException {

    this.key = key;
    fingerprints.put(MessageDigestAlgorithms.MD5, buildMD5Fingerprint());
    fingerprints.put(MessageDigestAlgorithms.SHA_256, buildSHA256Fingerprint());
  }

  public String getKey() {

    return key;
  }

  public String getMD5Fingerprint() {

    return fingerprints.get(MessageDigestAlgorithms.MD5);
  }

  public String getFormattedMD5Fingerprint() {

    return String.join(":", fingerprints.get(MessageDigestAlgorithms.MD5).split("(?<=\\G..)"));
  }

  public String getSHA256Fingerprint() {

    return fingerprints.get(MessageDigestAlgorithms.SHA_256);
  }

  private String buildMD5Fingerprint() throws InvalidSshKeyException {

    String fingerprint;

    try {

      byte[] decodedKey = Base64.getDecoder().decode(key);
      byte[] digest = MessageDigest.getInstance(MessageDigestAlgorithms.MD5).digest(decodedKey);
      fingerprint = Hex.encodeHexString(digest);

    } catch (NoSuchAlgorithmException e) {

      throw new InvalidSshKeyException("Error during fingerprint generation: unsupported algorithm",
          e);

    } catch (IllegalArgumentException iae) {

      throw new InvalidSshKeyException(
          "Error during fingerprint generation: RSA key is not base64 encoded", iae);
    }

    return fingerprint;
  }

  private String buildSHA256Fingerprint() throws InvalidSshKeyException {

    String fingerprint;

    try {

      byte[] decodedKey = Base64.getDecoder().decode(key);
      byte[] digest = MessageDigest.getInstance(MessageDigestAlgorithms.SHA_256).digest(decodedKey);
      fingerprint = Base64.getEncoder().encodeToString(digest);

    } catch (NoSuchAlgorithmException e) {

      throw new InvalidSshKeyException("Error during fingerprint generation: unsupported algorithm",
          e);

    } catch (IllegalArgumentException iae) {

      throw new InvalidSshKeyException(
          "Error during fingerprint generation: RSA key is not base64 encoded", iae);
    }

    return fingerprint;
  }
}
