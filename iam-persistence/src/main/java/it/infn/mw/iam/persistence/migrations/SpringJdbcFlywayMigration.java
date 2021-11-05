package it.infn.mw.iam.persistence.migrations;

import org.springframework.jdbc.core.JdbcTemplate;

@FunctionalInterface
public interface SpringJdbcFlywayMigration {

  void migrate(JdbcTemplate jdbcTemplate) throws Exception;
}
