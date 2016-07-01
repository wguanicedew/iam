package it.infn.mw.iam.test.util;

import org.junit.Assert;
import org.junit.Test;

import it.infn.mw.iam.test.TestUtils;
import it.infn.mw.iam.util.ssh.InvalidSshKeyException;
import it.infn.mw.iam.util.ssh.RSAPublicKeyUtils;

public class RSAPublicKeyTests {

  @Test
  public void testMD5Fingerprint() {

    String fp = RSAPublicKeyUtils.getMD5Fingerprint(TestUtils.getSshKey());
    Assert.assertEquals(fp, TestUtils.getSshKeyMD5Fingerprint());
  }

  @Test
  public void testFormattedMD5Fingerprint() {

    String fp = RSAPublicKeyUtils.getFormattedMD5Fingerprint(TestUtils.getSshKey());
    Assert.assertEquals(fp, TestUtils.getSshKeyFormattedMD5Fingerprint());
  }

  @Test
  public void testSHA256Fingerprint() {

    String fp = RSAPublicKeyUtils.getSHA256Fingerprint(TestUtils.getSshKey());
    Assert.assertEquals(fp, TestUtils.getSshKeySHA256Fingerprint());
  }

  @Test
  public void testMD5FingerprintInvalidKeyError() {

    try {
      RSAPublicKeyUtils.getMD5Fingerprint("This is not an encoded base64 key");
      Assert.fail("InvalidSshKeyException expected");
    } catch (InvalidSshKeyException e) {
    }
  }
  
  @Test
  public void testFormattedMD5FingerprintInvalidKeyError() {

    try {
      RSAPublicKeyUtils.getFormattedMD5Fingerprint("This is not an encoded base64 key");
      Assert.fail("InvalidSshKeyException expected");
    } catch (InvalidSshKeyException e) {
    }
  }

  @Test
  public void testSHA26FingerprintInvalidKeyError() {

    try {
      RSAPublicKeyUtils.getSHA256Fingerprint("This is not an encoded base64 key");
      Assert.fail("InvalidSshKeyException expected");
    } catch (InvalidSshKeyException e) {
    }
  }
}
