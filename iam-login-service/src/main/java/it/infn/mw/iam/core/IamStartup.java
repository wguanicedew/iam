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

    LOG.info("Starting Group manager authority check");
    
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

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {

    createGroupManagerAuthorities();
  }

}
