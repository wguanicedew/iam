package it.infn.mw.iam.test.ext_authn.saml.jit_account_provisioning;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isIn;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Optional;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import it.infn.mw.iam.config.saml.IamSamlProperties.IamSamlJITAccountProvisioningProperties;

public class JitPropertiesTests {


  @Test
  public void testTrustedIdpsListIsByDefaultEmpty() {
    IamSamlJITAccountProvisioningProperties props = new IamSamlJITAccountProvisioningProperties();

    Assert.assertFalse(props.getTrustedIdpsAsOptionalSet().isPresent());
    
    props.setTrustedIdps("all");
    
    assertFalse(props.getTrustedIdpsAsOptionalSet().isPresent()); 
  }
  
  
  @Test
  public void testTrustedIdpsListParsing() {
    IamSamlJITAccountProvisioningProperties props = new IamSamlJITAccountProvisioningProperties();
    
    props.setTrustedIdps("idp1,idp2,idp3,,,    ");
    
    Optional<Set<String>> trustedIdps = props.getTrustedIdpsAsOptionalSet(); 
    
    assertTrue(trustedIdps.isPresent());
    
    assertThat(trustedIdps.get(), hasSize(3));
    assertThat("idp1", isIn(trustedIdps.get()));
    assertThat("idp2", isIn(trustedIdps.get()));
    assertThat("idp3", isIn(trustedIdps.get()));
  }
  
  
  @Test
  public void testTrustedIdpsEmptyListYeldsEmptyOptional() {
    IamSamlJITAccountProvisioningProperties props = new IamSamlJITAccountProvisioningProperties();
    
    props.setTrustedIdps("");
    Optional<Set<String>> trustedIdps = props.getTrustedIdpsAsOptionalSet(); 
    assertFalse(trustedIdps.isPresent());
    
  }

}
