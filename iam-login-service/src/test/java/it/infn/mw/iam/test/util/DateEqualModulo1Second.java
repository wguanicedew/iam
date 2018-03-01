package it.infn.mw.iam.test.util;

import java.util.Date;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class DateEqualModulo1Second extends TypeSafeMatcher<Date>  {

  private final Date expectedValue;
  
  public DateEqualModulo1Second(Date expectedValue) {
    this.expectedValue = expectedValue;
  }

  @Override
  public void describeTo(Description description) {
    description.appendValue(expectedValue);
    
  }

  @Override
  protected boolean matchesSafely(Date actualValue) {
    
    if (expectedValue == null) {
      return actualValue == null;
    }
    
    if (actualValue == null) {
      return false;
    }
    
    long actualTime = actualValue.getTime();
    long expectedTime = expectedValue.getTime();
    
    long diff = Math.abs(actualTime-expectedTime);
    
    return diff < 1000;
  }


}
