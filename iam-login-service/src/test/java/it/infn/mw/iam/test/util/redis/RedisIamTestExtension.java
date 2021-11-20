package it.infn.mw.iam.test.util.redis;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class RedisIamTestExtension
    implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {

  private final RedisContainer redis = new RedisContainer();

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    redis.stop();
  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    redis.start();
    System.setProperty("spring.redis.port", String.valueOf(redis.getFirstMappedPort()));
    System.setProperty("spring.session.store-type", "redis");
  }

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    redis.start();
    System.setProperty("spring.redis.port", String.valueOf(redis.getFirstMappedPort()));
    System.setProperty("spring.session.store-type", "redis");

  }

  @Override
  public void afterAll(ExtensionContext context) throws Exception {
    redis.stop();
  }

}
