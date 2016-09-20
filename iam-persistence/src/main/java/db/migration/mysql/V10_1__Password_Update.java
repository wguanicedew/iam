package db.migration.mysql;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class V10_1__Password_Update implements SpringJdbcMigration {

  public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    SqlRowSet accountList =
        jdbcTemplate.queryForRowSet("SELECT id, username, password FROM iam_account");

    while (accountList.next()) {
      String userPass = accountList.getString("password");
      Long id = accountList.getLong("id");

      String userPassEncrypted = passwordEncoder.encode(userPass);

      if (passwordEncoder.matches(userPass, userPassEncrypted)) {
        jdbcTemplate.update("UPDATE iam_account SET password=? WHERE id=?", userPassEncrypted, id);
      }
    }
  }

}
