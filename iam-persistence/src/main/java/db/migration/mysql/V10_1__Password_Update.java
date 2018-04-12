/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2018
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
