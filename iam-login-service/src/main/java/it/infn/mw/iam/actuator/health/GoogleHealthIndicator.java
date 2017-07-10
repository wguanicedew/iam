package it.infn.mw.iam.actuator.health;

import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.stereotype.Component;

@Component
public class GoogleHealthIndicator extends AbstractHealthIndicator {

  private final String googleEndpoint;
  private final int timeout;

  @Autowired
  public GoogleHealthIndicator(@Value("${health.googleEndpoint}") String googleEndpoint,
      @Value("${health.timeout}") int timeout) {
    this.googleEndpoint = googleEndpoint;
    this.timeout = timeout;
  }

  @Override
  protected void doHealthCheck(Builder builder) throws Exception {
    builder.withDetail("location", googleEndpoint);

    HttpURLConnection conn = (HttpURLConnection) new URL(googleEndpoint).openConnection();
    conn.setRequestMethod("HEAD");
    conn.setConnectTimeout(timeout);
    int responseCode = conn.getResponseCode();
    if (responseCode != 200) {
      builder.down();
    } else {
      builder.up();
    }
  }

}
