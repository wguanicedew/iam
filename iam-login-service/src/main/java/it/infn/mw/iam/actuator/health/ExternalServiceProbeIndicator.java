package it.infn.mw.iam.actuator.health;

import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.stereotype.Component;

@Component
public class ExternalServiceProbeIndicator extends AbstractHealthIndicator {

  private final String endpoint;
  private final int timeout;

  @Autowired
  public ExternalServiceProbeIndicator(@Value("${health.externalServiceProbe.endpoint}") String endpoint,
      @Value("${health.externalServiceProbe.timeout}") int timeout) {
    this.endpoint = endpoint;
    this.timeout = timeout;
  }

  @Override
  protected void doHealthCheck(Builder builder) throws Exception {
    builder.withDetail("location", endpoint);

    HttpURLConnection conn = (HttpURLConnection) new URL(endpoint).openConnection();
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
