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
package it.infn.mw.iam.test.model;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mitre.oauth2.model.OAuth2AccessTokenEntity;
import org.mitre.oauth2.model.OAuth2RefreshTokenEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.core.user.IamAccountService;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamUserInfo;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.persistence.repository.IamOAuthAccessTokenRepository;
import it.infn.mw.iam.persistence.repository.IamOAuthRefreshTokenRepository;
import it.infn.mw.iam.test.api.tokens.TestTokensUtils;
import it.infn.mw.iam.test.core.CoreControllerTestSupport;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class, CoreControllerTestSupport.class})
@WebAppConfiguration
@Transactional
public class OrphanTokensTests extends TestTokensUtils {

  public static final Logger LOG = LoggerFactory.getLogger(OrphanTokensTests.class);

  private static final String ORPHAN_USERNAME = "hpotter";
  private static final String ORPHAN_GIVENNAME = "Harry";
  private static final String ORPHAN_FAMILYNAME = "Potter";

  @Autowired
  private IamAccountService accountService;
  @Autowired
  private IamOAuthAccessTokenRepository accessTokensRepo;
  @Autowired
  private IamOAuthRefreshTokenRepository refreshTokensRepo;
  @Autowired
  private IamAccountRepository accountRepo;

  private IamAccount newAccount(String givenName, String familyName, String username) {
    IamAccount account = new IamAccount();
    account.setUsername(username);
    account.setUuid(UUID.randomUUID().toString());
    IamUserInfo userInfo = new IamUserInfo();
    userInfo.setEmail(username + "@email.com");
    userInfo.setGivenName(givenName);
    userInfo.setFamilyName(familyName);
    userInfo.setIamAccount(account);
    account.setUserInfo(userInfo);
    return account;
  }

  private OAuth2AccessTokenEntity createOrphanAccessToken() {

    final String TEST_CLIENT_ID = "token-lookup-client";
    final String[] SCOPES = {"openid", "profile"};
    IamAccount orphan = newAccount(ORPHAN_GIVENNAME, ORPHAN_FAMILYNAME, ORPHAN_USERNAME);
    LOG.info("Creating new account {}", orphan);
    accountService.createAccount(orphan);
    LOG.info("Account exists? {}", accountRepo.findByUsername(ORPHAN_USERNAME).isPresent());
    OAuth2AccessTokenEntity token = buildAccessToken(loadTestClient(TEST_CLIENT_ID), orphan.getUsername(), SCOPES);
    LOG.info("Token name: {}", token.getAuthenticationHolder().getAuthentication().getName());
    LOG.info("Created access token with id {}", accessTokensRepo.findAll().iterator().next().getId());
    LOG.info("Deleting account ...");
    accountRepo.delete(orphan);
    LOG.info("Account exists? {}", accountRepo.findByUsername(ORPHAN_USERNAME).isPresent());
    return token;
  }

  private OAuth2RefreshTokenEntity createOrphanRefreshToken() {

    final String TEST_CLIENT_ID = "token-lookup-client";
    final String[] SCOPES = {"openid", "profile", "offline_access"};
    IamAccount orphan = newAccount(ORPHAN_GIVENNAME, ORPHAN_FAMILYNAME, ORPHAN_USERNAME);
    LOG.info("Creating new account {}", orphan);
    accountService.createAccount(orphan);
    LOG.info("Account exists? {}", accountRepo.findByUsername(ORPHAN_USERNAME).isPresent());
    OAuth2AccessTokenEntity token = buildAccessToken(loadTestClient(TEST_CLIENT_ID), orphan.getUsername(), SCOPES);
    LOG.info("Created access token with id {}", accessTokensRepo.findAll().iterator().next().getId());
    LOG.info("with refresh token with id {}", refreshTokensRepo.findAll().iterator().next().getId());
    LOG.info("Deleting account ...");
    accountRepo.delete(orphan);
    return token.getRefreshToken();
  }

  @After
  public void teardown() {

    clearAllTokens();
  }

  @Test
  public void assumeNoOrphanTokenIsFoundAtStart() {

    assertThat(accessTokensRepo.findOrphanedTokens().size(), equalTo(0));
    assertThat(refreshTokensRepo.findOrphanedTokens().size(), equalTo(0));
    assertThat(accessTokensRepo.count(), equalTo(0L));
    assertThat(refreshTokensRepo.count(), equalTo(0L));
  }

  @Test
  public void findAndDeleteOrphanAccessTokensTest() {

    clearAllTokens();

    /*
     * Steps to create an orphan token: - Create a test user - build a valid access token - remove
     * only the user
     */
    createOrphanAccessToken();

    assertThat(accessTokensRepo.count(), equalTo(1L));
    assertThat(accessTokensRepo.findAll()
      .iterator()
      .next()
      .getAuthenticationHolder()
      .getAuthentication()
      .getName(), equalTo(ORPHAN_USERNAME));

    assertThat(accountRepo.findByUsername(ORPHAN_USERNAME).isPresent(), equalTo(false));

    List<OAuth2AccessTokenEntity> orphanTokens =
        accessTokensRepo.findOrphanedTokens();

    assertThat(orphanTokens.size(), equalTo(1));

    assertThat(orphanTokens.get(0).getAuthenticationHolder().getAuthentication().getName(),
        equalTo(ORPHAN_USERNAME));

    accessTokensRepo.delete(orphanTokens);

    assertThat(accessTokensRepo.findOrphanedTokens().size(), equalTo(0));
  }

  @Test
  public void findAndDeleteOrphanRefreshTokensTest() {

    clearAllTokens();

    /*
     * Steps to create an orphan token: - Create a test user - build a valid access token - remove
     * only the user
     */
    createOrphanRefreshToken();

    /* check access-token exists and it's owned by test user */
    assertThat(refreshTokensRepo.count(), equalTo(1L));
    assertThat(refreshTokensRepo.findAll()
      .iterator()
      .next()
      .getAuthenticationHolder()
      .getAuthentication()
      .getName(), equalTo(ORPHAN_USERNAME));

    /* find orphan tokens */
    List<OAuth2RefreshTokenEntity> orphanTokens =
        refreshTokensRepo.findOrphanedTokens();

    assertThat(orphanTokens.size(), equalTo(1));

    /* check orphan owner is the test user */
    assertThat(orphanTokens.get(0).getAuthenticationHolder().getAuthentication().getName(),
        equalTo(ORPHAN_USERNAME));
    /* check test user doesn't exist */
    assertThat(accountRepo.findByUsername(ORPHAN_USERNAME).isPresent(), equalTo(false));

    /* remove orphan tokens */
    refreshTokensRepo.delete(orphanTokens);

    assertThat(refreshTokensRepo.findOrphanedTokens().size(), equalTo(0));
  }
}
