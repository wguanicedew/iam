package it.infn.mw.iam.actuator.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.boot.actuate.health.CompositeHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.OrderedHealthAggregator;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import it.infn.mw.iam.actuator.health.GoogleHealthIndicator;

@Component
@ConfigurationProperties(prefix = "endpoints.externalService")
public class ExternalServiceHealthEndpoint extends AbstractEndpoint<Health> {

  private static final String ENDPOINT_ID = "externalService";

  private final HealthIndicator healthIndicator;

  @Autowired
  private HealthAggregator healthAggregator = new OrderedHealthAggregator();

  @Autowired
  public ExternalServiceHealthEndpoint(GoogleHealthIndicator googleHealthIndicator) {
    super(ENDPOINT_ID, false);

    CompositeHealthIndicator indicator = new CompositeHealthIndicator(healthAggregator);
    indicator.addHealthIndicator("google", googleHealthIndicator);

    this.healthIndicator = indicator;
  }

  @Override
  public Health invoke() {
    return this.healthIndicator.health();
  }
}
