package it.infn.mw.iam.test.util;

import it.infn.mw.iam.core.time.TimeProvider;

public class MockTimeProvider implements TimeProvider {

  private long currentTimeMillis = System.currentTimeMillis();

  @Override
  public long currentTimeMillis() {
    return currentTimeMillis;
  }

  public void setTime(long timeMillis) {
    currentTimeMillis = timeMillis;
  }

}
