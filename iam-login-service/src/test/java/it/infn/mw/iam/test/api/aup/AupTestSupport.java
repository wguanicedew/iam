package it.infn.mw.iam.test.api.aup;

import java.util.Date;

import it.infn.mw.iam.persistence.model.IamAup;

public class AupTestSupport {
  
  public static final String DEFAULT_AUP_NAME = "default-aup";
  public static final String DEFAULT_AUP_TEXT = "default-aup-text";
  public static final String DEFAULT_AUP_DESC = "default-aup-desc";


  public IamAup buildDefaultAup() {
    Date now = new Date();
    IamAup aup = new IamAup();
    
    aup.setName(DEFAULT_AUP_NAME);
    aup.setText(DEFAULT_AUP_TEXT);
    aup.setDescription(DEFAULT_AUP_DESC);
    aup.setCreationTime(now);
    aup.setLastUpdateTime(now);
    aup.setSignatureValidityInDays(365L);
    
    return aup;
  }

}
