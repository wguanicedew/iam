package it.infn.mw.iam.test.util;

import org.springframework.test.annotation.ProfileValueSource;
import org.springframework.util.Assert;

public class NullSafeSystemProfileValueSource implements ProfileValueSource {

  public NullSafeSystemProfileValueSource() {
  }

  @Override
  public String get(String key) {
    Assert.hasText(key, "'key' must not be empty");
    
    String val = System.getProperty(key);
    if (val == null) {
      return "<null>";
    }
    
    return val;
  }

}
