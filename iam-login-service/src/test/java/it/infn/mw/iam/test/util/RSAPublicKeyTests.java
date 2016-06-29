package it.infn.mw.iam.test.util;

import org.junit.Assert;
import org.junit.Test;

import it.infn.mw.iam.test.TestUtils;
import it.infn.mw.iam.util.ssh.RSAPublicKey;

public class RSAPublicKeyTests {

  @Test
  public void testMD5Fingerprint() {

    RSAPublicKey key = new RSAPublicKey(TestUtils.getSshKey());

    String fp = key.getMD5Fingerprint();
    Assert.assertEquals(fp, TestUtils.getSshKeyMD5Fingerprint());

    fp = key.getFormattedMD5Fingerprint();
    Assert.assertEquals(fp, TestUtils.getSshKeyFormattedMD5Fingerprint());
  }

  @Test
  public void testSHA256Fingerprint() {

    RSAPublicKey key = new RSAPublicKey(TestUtils.getSshKey());

    String fp = key.getSHA256Fingerprint();
    Assert.assertEquals(fp, TestUtils.getSshKeySHA256Fingerprint());
  }
}
