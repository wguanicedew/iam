package it.infn.mw.iam.core.time;

/**
 * Time provider interface.
 *
 */
public interface TimeProvider {

  /**
   * Returns the current time in milliseconds
   * 
   * @return the difference, measured in milliseconds, between the current time and midnight,
   * January 1, 1970 UTC.
   * 
   * @see System#currentTimeMillis()
   */
  public long currentTimeMillis();

}
