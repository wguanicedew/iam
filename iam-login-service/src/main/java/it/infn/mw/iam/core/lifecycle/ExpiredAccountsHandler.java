/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2019
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
package it.infn.mw.iam.core.lifecycle;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import it.infn.mw.iam.config.lifecycle.LifecycleProperties;
import it.infn.mw.iam.core.user.IamAccountService;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamLabel;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

@Component
public class ExpiredAccountsHandler implements Runnable {

  public enum AccountLifecycleStatus {
    OK,
    PENDING_SUSPENSION,
    PENDING_REMOVAL
  }

  public static final String LIFECYCLE_TIMESTAMP_LABEL = "lifecycle.timestamp";
  public static final String LIFECYCLE_STATUS_LABEL = "lifecycle.status";
  public static final String LIFECYCLE_IGNORE_LABEL = "lifecycle.ignore";
  public static final String LIFECYCLE_MESSAGE_LABEL = "lifecycle.message";

  public static final int PAGE_SIZE = 5;

  public static final Logger LOG = LoggerFactory.getLogger(ExpiredAccountsHandler.class);

  private final LifecycleProperties properties;
  private final IamAccountRepository accountRepo;
  private final IamAccountService accountService;
  private final Clock clock;

  private Instant checkTime;

  @Autowired
  public ExpiredAccountsHandler(Clock clock, LifecycleProperties properties,
      IamAccountRepository repo, IamAccountService service) {
    this.clock = clock;
    this.properties = properties;
    this.accountRepo = repo;
    this.accountService = service;
  }

  private boolean pastGracePeriod(IamAccount expiredAccount, long gracePeriodDays) {
    final Instant endTime = expiredAccount.getEndTime().toInstant();

    if (gracePeriodDays > 0) {
      return checkTime.isAfter(endTime.plus(gracePeriodDays, ChronoUnit.DAYS));
    }

    return true;
  }

  private boolean pastSuspensionGracePeriod(IamAccount expiredAccount) {
    return pastGracePeriod(expiredAccount,
        properties.getAccount().getExpiredAccountPolicy().getSuspensionGracePeriodDays());
  }

  private boolean pastRemovalGracePeriod(IamAccount expiredAccount) {
    return pastGracePeriod(expiredAccount,
        properties.getAccount().getExpiredAccountPolicy().getRemovalGracePeriodDays());
  }

  private void addLastCheckedLabel(IamAccount expiredAccount) {
    accountService.setLabel(expiredAccount,
        IamLabel.builder()
          .name(LIFECYCLE_TIMESTAMP_LABEL)
          .value(String.valueOf(checkTime.toEpochMilli()))
          .build());
  }

  private void addStatusLabel(IamAccount expiredAccount, AccountLifecycleStatus status) {
    accountService.setLabel(expiredAccount,
        IamLabel.builder().name(LIFECYCLE_STATUS_LABEL).value(status.name()).build());
  }

  private void suspendAccount(IamAccount expiredAccount) {

    LOG.info("Suspeding account {} expired on {} ({} days ago)", expiredAccount.getUsername(),
        expiredAccount.getEndTime(),
        ChronoUnit.DAYS.between(expiredAccount.getEndTime().toInstant(), checkTime));
    accountService.disableAccount(expiredAccount);
    addStatusLabel(expiredAccount, AccountLifecycleStatus.PENDING_REMOVAL);
    addLastCheckedLabel(expiredAccount);
  }

  private void markAsPendingSuspension(IamAccount expiredAccount) {
    LOG.info("Marking account {} (expired on {} ({} days ago)) as pending suspension",
        expiredAccount.getUsername(), expiredAccount.getEndTime(),
        ChronoUnit.DAYS.between(expiredAccount.getEndTime().toInstant(), checkTime));
    addStatusLabel(expiredAccount, AccountLifecycleStatus.PENDING_SUSPENSION);
    addLastCheckedLabel(expiredAccount);
  }

  private void removeAccount(IamAccount expiredAccount) {
    LOG.info("Removing account {} expired on {} ({} days ago)", expiredAccount.getUsername(),
        expiredAccount.getEndTime(),
        ChronoUnit.DAYS.between(expiredAccount.getEndTime().toInstant(), checkTime));
    accountService.deleteAccount(expiredAccount);
  }


  private void handleExpiredAccount(IamAccount expiredAccount) {

    if (pastRemovalGracePeriod(expiredAccount)) {
      removeAccount(expiredAccount);
    } else if (pastSuspensionGracePeriod(expiredAccount)) {
      suspendAccount(expiredAccount);
    } else {
      markAsPendingSuspension(expiredAccount);
    }
  }

  public void handleExpiredAccounts() {

    LOG.debug("Starting...");
    checkTime = clock.instant();
    Date now = Date.from(checkTime);

    Pageable pageRequest = new PageRequest(0, PAGE_SIZE);

    while (true) {
      Page<IamAccount> expiredAccountsPage =
          accountRepo.findExpiredAccountsAtTimestamp(now, pageRequest);
      LOG.debug("expiredAccountsPage: {}", expiredAccountsPage);

      if (expiredAccountsPage.hasContent()) {
        // The old version of EclipseLink may have issues if using foreach
        for (IamAccount expiredAccount : expiredAccountsPage.getContent()) {
          handleExpiredAccount(expiredAccount);
        }
      }

      if (!expiredAccountsPage.hasNext()) {
        break;
      }

      pageRequest = expiredAccountsPage.nextPageable();
    }
  }

  @Override
  public void run() {
    handleExpiredAccounts();
  }
}
