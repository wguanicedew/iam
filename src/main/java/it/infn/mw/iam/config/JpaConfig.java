package it.infn.mw.iam.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;

@Configuration
public class JpaConfig extends JpaBaseConfiguration {

  @Autowired
  DataSource dataSource;

  @Override
  protected AbstractJpaVendorAdapter createJpaVendorAdapter() {

    return new EclipseLinkJpaVendorAdapter();
  }

  @Override
  protected Map<String, Object> getVendorProperties() {

    Map<String, Object> map = new HashMap<String, Object>();

    map.put("eclipselink.weaving", "false");
    map.put("eclipselink.logging.level", "INFO");
    map.put("eclipselink.logging.level.sql", "INFO");
    map.put("eclipselink.cache.shared.default", "false");

    return map;

  }

  @Override
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
    EntityManagerFactoryBuilder factoryBuilder) {

    return factoryBuilder.dataSource(dataSource)
      .packages("org.mitre")
      .persistenceUnit("defaultPersistenceUnit")
      .properties(getVendorProperties()).build();

  }
}
