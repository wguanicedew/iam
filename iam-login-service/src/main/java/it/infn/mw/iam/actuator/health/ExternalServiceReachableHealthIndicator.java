package it.infn.mw.iam.actuator.health;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import it.infn.mw.iam.config.IamProperties;

@Component
@ConditionalOnProperty(name = "iam.external-service-probe.enabled", havingValue = "true")
public class ExternalServiceReachableHealthIndicator implements HealthIndicator {

  private final String endpoint;
  private final int timeoutInSecs;

  @Autowired
  public ExternalServiceReachableHealthIndicator(IamProperties properties) {
    this.endpoint = properties.getExternalServiceProbe().getEndpoint();
    this.timeoutInSecs = properties.getExternalServiceProbe().getTimeoutInSecs();
  }

  @Override
  public Health health() {
    try {
      HttpURLConnection conn = (HttpURLConnection) new URL(endpoint).openConnection();
      conn.setRequestMethod("HEAD");
      conn.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(timeoutInSecs));

      int responseCode = conn.getResponseCode();
      if (responseCode == 200) {
        return Health.up().withDetail("endpoint", endpoint).build();
      } else {
        return Health.down().withDetail("endpoint", endpoint).build();
      }
    } catch (IOException e) {
      return Health.down(e).build();
    }
  }
}
