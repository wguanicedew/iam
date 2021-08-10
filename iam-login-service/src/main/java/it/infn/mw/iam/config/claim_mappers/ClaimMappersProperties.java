package it.infn.mw.iam.config.claim_mappers;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties("claim-mappers")
@Validated
public class ClaimMappersProperties {

}
