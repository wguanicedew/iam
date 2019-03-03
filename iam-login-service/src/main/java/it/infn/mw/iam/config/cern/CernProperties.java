package it.infn.mw.iam.config.cern;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("cern")
public class CernProperties {

  public static class HrDbApiProperties {

    String url;
    String username;
    String password;

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }
  }

  private String ssoEntityId = "https://cern.ch/login";

  private String experimentName;
  
  private HrDbApiProperties hrApi;

  public String getSsoEntityId() {
    return ssoEntityId;
  }

  public void setSsoEntityId(String ssoEntityId) {
    this.ssoEntityId = ssoEntityId;
  }

  public HrDbApiProperties getHrApi() {
    return hrApi;
  }

  public void setHrApi(HrDbApiProperties hrApi) {
    this.hrApi = hrApi;
  }

  public String getExperimentName() {
    return experimentName;
  }

  public void setExperimentName(String experimentName) {
    this.experimentName = experimentName;
  }
}
