package it.infn.mw.iam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication

@ComponentScan(basePackages = { 
  "it.infn.mw.iam.config",
  "org.mitre.oauth2.web",
  "org.mitre.oauth2.view",
  "org.mitre.openid.connect.web",
  "org.mitre.openid.connect.view",
  "org.mitre.discovery.view" })
public class MitreidConnectBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(MitreidConnectBootApplication.class, args);
	}
}
