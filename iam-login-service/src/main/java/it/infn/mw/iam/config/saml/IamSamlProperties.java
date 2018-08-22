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
package it.infn.mw.iam.config.saml;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;

import org.springframework.boot.context.properties.ConfigurationProperties;

import it.infn.mw.iam.config.login.LoginButtonProperties;

@ConfigurationProperties(prefix = "saml")
public class IamSamlProperties {

  private String entityId;

  private String keystore;

  private String keystorePassword;

  private String keyId;

  private String keyPassword;

  private String idResolvers;

  private int maxAssertionTimeSec;

  private int maxAuthenticationAgeSec;

  private int metadataLookupServiceRefreshPeriodSec = (int) TimeUnit.MINUTES.toSeconds(5);

  private String idpEntityIdWhilelist;

  private List<IamSamlIdpMetadataProperties> idpMetadata;

  @Valid
  private List<IamSamlLoginShortcut> loginShortcuts;
  
  private LoginButtonProperties wayfLoginButton;

  public List<IamSamlIdpMetadataProperties> getIdpMetadata() {
    return idpMetadata;
  }

  public void setIdpMetadata(List<IamSamlIdpMetadataProperties> idpMetadata) {
    this.idpMetadata = idpMetadata;
  }

  public String getEntityId() {
    return entityId;
  }

  public void setEntityId(String entityId) {
    this.entityId = entityId;
  }

  public String getKeystore() {
    return keystore;
  }

  public void setKeystore(String keystore) {
    this.keystore = keystore;
  }

  public String getKeystorePassword() {
    return keystorePassword;
  }

  public void setKeystorePassword(String keystorePassword) {
    this.keystorePassword = keystorePassword;
  }

  public String getKeyId() {
    return keyId;
  }

  public void setKeyId(String keyId) {
    this.keyId = keyId;
  }

  public String getKeyPassword() {
    return keyPassword;
  }

  public void setKeyPassword(String keyPassword) {
    this.keyPassword = keyPassword;
  }

  public int getMaxAssertionTimeSec() {
    return maxAssertionTimeSec;
  }

  public void setMaxAssertionTimeSec(int maxAssertionTimeSec) {
    this.maxAssertionTimeSec = maxAssertionTimeSec;
  }

  public int getMaxAuthenticationAgeSec() {
    return maxAuthenticationAgeSec;
  }

  public void setMaxAuthenticationAgeSec(int maxAuthenticationAgeSec) {
    this.maxAuthenticationAgeSec = maxAuthenticationAgeSec;
  }

  public String getIdResolvers() {
    return idResolvers;
  }

  public void setIdResolvers(String idResolvers) {
    this.idResolvers = idResolvers;
  }

  public int getMetadataLookupServiceRefreshPeriodSec() {
    return metadataLookupServiceRefreshPeriodSec;
  }

  public void setMetadataLookupServiceRefreshPeriodSec(int metadataLookupServiceRefreshPeriodSec) {
    this.metadataLookupServiceRefreshPeriodSec = metadataLookupServiceRefreshPeriodSec;
  }


  public String getIdpEntityIdWhilelist() {
    return idpEntityIdWhilelist;
  }

  public void setIdpEntityIdWhilelist(String idpEntityIdWhilelist) {
    this.idpEntityIdWhilelist = idpEntityIdWhilelist;
  }

  public List<IamSamlLoginShortcut> getLoginShortcuts() {
    return loginShortcuts;
  }

  public void setLoginShortcuts(List<IamSamlLoginShortcut> loginShortcuts) {
    this.loginShortcuts = loginShortcuts;
  }

  public LoginButtonProperties getWayfLoginButton() {
    return wayfLoginButton;
  }

  public void setWayfLoginButton(LoginButtonProperties wayfLoginButton) {
    this.wayfLoginButton = wayfLoginButton;
  }

}
