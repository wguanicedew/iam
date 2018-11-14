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
package it.infn.mw.iam.core;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import it.infn.mw.iam.persistence.model.IamAuthority;
import it.infn.mw.iam.persistence.model.IamGroup;
import it.infn.mw.iam.persistence.repository.IamAuthoritiesRepository;
import it.infn.mw.iam.persistence.repository.IamGroupRepository;

@Component
public class IamStartup implements ApplicationListener<ApplicationReadyEvent> {

  public static final Logger LOG = LoggerFactory.getLogger(IamStartup.class);
  final IamGroupRepository groupRepo;
  final IamAuthoritiesRepository authoritiesRepo;

  @Autowired
  public IamStartup(IamGroupRepository groupRepository,
      IamAuthoritiesRepository authoritiesRepository) {
    this.groupRepo = groupRepository;
    this.authoritiesRepo = authoritiesRepository;
  }

  @Transactional
  public void createGroupManagerAuthorities() {

    LOG.debug("Starting Group manager authority check");
    
    for (IamGroup g : groupRepo.findAll()) {

      String groupManagerAuthority = String.format("ROLE_GM:%s", g.getUuid());

      if (!authoritiesRepo.findByAuthority(groupManagerAuthority).isPresent()) {
        LOG.info("Creating Group Manager authority for group '{}' with uuid '{}'", g.getName(),
            g.getUuid());
        IamAuthority auth = new IamAuthority(groupManagerAuthority);
        authoritiesRepo.save(auth);
      }
    }
  }
  
  @Transactional
  public void dropOrphanedTokens() {
    
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {

    // createGroupManagerAuthorities();
  }

}
