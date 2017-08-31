package it.infn.mw.iam.test.ext_authn.saml.jit_account_provisioning;

import static com.google.common.base.Preconditions.checkArgument;
import static it.infn.mw.iam.test.ext_authn.saml.SamlAuthenticationTestSupport.DEFAULT_IDP_ID;
import static it.infn.mw.iam.test.ext_authn.saml.SamlAuthenticationTestSupport.T1_EPUID;

import java.util.Random;

import it.infn.mw.iam.authn.saml.util.Saml2Attribute;
import it.infn.mw.iam.persistence.model.IamSamlId;

public class JitUserDetailsServiceTestsSupport {
  
  public static final IamSamlId T1_SAML_ID =
      new IamSamlId(DEFAULT_IDP_ID, Saml2Attribute.EPUID.getAttributeName(), T1_EPUID);

  public static final IamSamlId LONG_SAML_ID = 
      new IamSamlId(DEFAULT_IDP_ID, Saml2Attribute.EPUID.getAttributeName(), generateRandomString(130));
  
 
  
  public static String generateRandomString(int length) {
    checkArgument(length > 0, "Please provide a positive length argument");
    char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    StringBuilder sb = new StringBuilder();
    Random random = new Random();
    
    for (int i = 0; i < length; i++) {
      char c = chars[random.nextInt(chars.length)];
      sb.append(c);
      
    }
    
    return sb.toString();
  }
  
  
          
}
