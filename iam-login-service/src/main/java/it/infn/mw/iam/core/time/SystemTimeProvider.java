package it.infn.mw.iam.core.time;

/**
 * 
 * A {@link TimeProvider} implementation that leverages {@link System#currentTimeMillis()} to return
 * the time.
 *
 */
public class SystemTimeProvider implements TimeProvider {

  public SystemTimeProvider() {}

  @Override
  public long currentTimeMillis() {

    return System.currentTimeMillis();
  }

}
