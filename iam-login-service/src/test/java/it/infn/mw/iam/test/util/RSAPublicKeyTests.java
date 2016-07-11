package it.infn.mw.iam.test.util;

import org.junit.Assert;
import org.junit.Test;

import it.infn.mw.iam.test.TestUtils;
import it.infn.mw.iam.util.ssh.InvalidSshKeyException;
import it.infn.mw.iam.util.ssh.RSAPublicKeyUtils;

public class RSAPublicKeyTests {

  @Test
  public void testMD5Fingerprint() {

    String fp = RSAPublicKeyUtils.getMD5Fingerprint(TestUtils.sshKeys.get(0).key);
    Assert.assertEquals(fp, TestUtils.sshKeys.get(0).fingerprintMDS);
    fp = RSAPublicKeyUtils.getMD5Fingerprint(TestUtils.sshKeys.get(1).key);
    Assert.assertEquals(fp, TestUtils.sshKeys.get(1).fingerprintMDS);
  }

  @Test
  public void testFormattedMD5Fingerprint() {

    String fp = RSAPublicKeyUtils.getFormattedMD5Fingerprint(TestUtils.sshKeys.get(0).key);
    Assert.assertEquals(fp, TestUtils.sshKeys.get(0).fingerprintMD5Formatted);
    fp = RSAPublicKeyUtils.getFormattedMD5Fingerprint(TestUtils.sshKeys.get(1).key);
    Assert.assertEquals(fp, TestUtils.sshKeys.get(1).fingerprintMD5Formatted);
  }

  @Test
  public void testSHA256Fingerprint() {

    String fp = RSAPublicKeyUtils.getSHA256Fingerprint(TestUtils.sshKeys.get(0).key);
    Assert.assertEquals(fp, TestUtils.sshKeys.get(0).fingerprintSHA256);
    fp = RSAPublicKeyUtils.getSHA256Fingerprint(TestUtils.sshKeys.get(1).key);
    Assert.assertEquals(fp, TestUtils.sshKeys.get(1).fingerprintSHA256);
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
