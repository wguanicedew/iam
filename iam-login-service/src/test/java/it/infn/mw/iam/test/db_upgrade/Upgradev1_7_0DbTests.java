package it.infn.mw.iam.test.db_upgrade;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.BindMode;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import it.infn.mw.iam.test.util.db.MySQL57TestContainer;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles({"mysql-test", "flyway-repair"})
@DirtiesContext
public class Upgradev1_7_0DbTests extends UpgradeDbTestSupport {

  public static final String DB_DUMP = "iam-v1.7.0-mysql5.7.sql";

  @Container
  static MySQL57TestContainer db =
      new MySQL57TestContainer().withClasspathResourceMapping(
          joinPathStrings(DB_DUMPS_DIR, DB_DUMP), joinPathStrings(INITDB_DIR, DB_DUMP),
          BindMode.READ_ONLY);


  @DynamicPropertySource
  static void registerMysqlConnectionString(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", db::getJdbcUrl);
  }


}
