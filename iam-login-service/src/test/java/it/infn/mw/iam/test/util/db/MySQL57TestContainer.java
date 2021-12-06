package it.infn.mw.iam.test.util.db;

import org.testcontainers.containers.MySQLContainer;

public class MySQL57TestContainer extends MySQLContainer<MySQL57TestContainer> {

  public static final String DEFAULT_IMAGE = "mysql:5.7";
  public static final String DEFAULT_DATABASE_NAME = "iam";
  public static final String DEFAULT_USERNAME = "iam";
  public static final String DEFAULT_PASSWORD = "pwd";

  public MySQL57TestContainer() {
    super(DEFAULT_IMAGE);
    withDatabaseName(DEFAULT_DATABASE_NAME);
    withPassword(DEFAULT_PASSWORD);
    withUsername(DEFAULT_USERNAME);
  }

}
