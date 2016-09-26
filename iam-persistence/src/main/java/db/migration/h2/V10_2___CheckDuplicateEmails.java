package db.migration.h2;

import java.util.ArrayList;
import java.util.List;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class V10_2___CheckDuplicateEmails implements SpringJdbcMigration {

  public static final Logger LOG = LoggerFactory.getLogger(V10_2___CheckDuplicateEmails.class);

  @Override
  public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

    SqlRowSet rowSet = jdbcTemplate
      .queryForRowSet("SELECT email from iam_user_info group by email having count(email) > 1");

    List<String> duplicateEmails = new ArrayList<>();

    while (rowSet.next()) {
      String email = rowSet.getString("email");
      duplicateEmails.add(email);
    }

    // Disable users with duplicate email
    if (!duplicateEmails.isEmpty()) {
      LOG.warn("### DUPLICATE EMAIL WARNING ###");
      LOG.warn("Found multiple accounts linked to the same email. "
          + "This upgrade script will change the email address for users");

      for (String email : duplicateEmails) {
        rowSet =
            jdbcTemplate.queryForRowSet(
                "SELECT iam_account.id, iam_account.uuid from iam_account, iam_user_info where "
                    + "iam_account.user_info_id = iam_user_info.id and iam_user_info.email = ?",
                email);

        while (rowSet.next()) {

          Integer accountId = rowSet.getInt("id");
          String accountUuid = rowSet.getString("uuid");

          String uniqueUpdatedEmail = String.format("%d_%s", accountId, email);

          int updateResult = jdbcTemplate.update(
              "update iam_user_info set iam_user_info.email = ? "
                  + "where id = (select user_info_id from iam_account where iam_account.id = ?)",
              uniqueUpdatedEmail, accountId);

          if (updateResult == 1) {
            LOG.warn("Updated email for IAM account {} (uuid: {}). New email: {}", accountId,
                accountUuid, uniqueUpdatedEmail);
          }
        }

      }
      LOG.warn("### END Of DUPLICATE EMAIL WARNING ###");
    }
  }

}
