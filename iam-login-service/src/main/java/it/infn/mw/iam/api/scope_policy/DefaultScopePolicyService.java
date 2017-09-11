package it.infn.mw.iam.api.scope_policy;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import it.infn.mw.iam.audit.events.scope_policy.ScopePolicyCreatedEvent;
import it.infn.mw.iam.audit.events.scope_policy.ScopePolicyDeletedEvent;
import it.infn.mw.iam.audit.events.scope_policy.ScopePolicyUpdatedEvent;
import it.infn.mw.iam.persistence.model.IamScopePolicy;
import it.infn.mw.iam.persistence.repository.IamScopePolicyRepository;

@Service
public class DefaultScopePolicyService implements ScopePolicyService {

  private final IamScopePolicyRepository scopePolicyRepo;
  private final IamScopePolicyConverter converter;
  private final ApplicationEventPublisher publisher;
  
  private ScopePolicyNotFoundError notFoundError(Long id) {
    return new ScopePolicyNotFoundError(String.format("No scope policy found for id: %d", id));
  }

  @Autowired
  public DefaultScopePolicyService(IamScopePolicyRepository scopePolicyRepo,
      IamScopePolicyConverter converter,
      ApplicationEventPublisher publisher) {
    this.scopePolicyRepo = scopePolicyRepo;
    this.converter = converter;
    this.publisher = publisher;
  }

  @Override
  public Iterable<IamScopePolicy> findAllScopePolicies() {
    return scopePolicyRepo.findAll();
  }


  @Override
  public Optional<IamScopePolicy> findScopePolicyById(Long policyId) {
    return scopePolicyRepo.findById(policyId);
  }

  @Override
  public void deleteScopePolicyById(Long policyId) {
    IamScopePolicy sp =
        scopePolicyRepo.findById(policyId).orElseThrow(() -> notFoundError(policyId));

    scopePolicyRepo.delete(sp);
    publisher.publishEvent(new ScopePolicyDeletedEvent(this, sp));
  }

  @Override
  public IamScopePolicy createScopePolicy(ScopePolicyDTO scopePolicy) {
    IamScopePolicy sp = converter.toModel(scopePolicy);
    Date now = new Date();
    sp.setCreationTime(now);
    sp.setLastUpdateTime(now);

    List<IamScopePolicy> equivalentPolicies = scopePolicyRepo.findEquivalentPolicies(sp);

    if (!equivalentPolicies.isEmpty()) {
      throw new DuplicateScopePolicyError(equivalentPolicies);
    }

    sp.linkAccount();
    sp.linkGroup();

    sp = scopePolicyRepo.save(sp);
    publisher.publishEvent(new ScopePolicyCreatedEvent(this, sp));
    return sp;

  }

  @Override
  public IamScopePolicy updateScopePolicy(ScopePolicyDTO scopePolicy) {
    IamScopePolicy updatedPolicy = converter.toModel(scopePolicy);
    
    IamScopePolicy existingPolicy =
        scopePolicyRepo.findById(scopePolicy.getId()).orElseThrow(() -> notFoundError(scopePolicy.getId()));
    
    List<IamScopePolicy> equivalentPolicies = scopePolicyRepo.findEquivalentPolicies(updatedPolicy);
    
    // The new policy can be equivalent to the existing policy it will replace 
    equivalentPolicies.remove(existingPolicy);
    
    if (!equivalentPolicies.isEmpty()){
      throw new DuplicateScopePolicyError(equivalentPolicies);
    }
    
    existingPolicy.from(updatedPolicy);
    existingPolicy.setLastUpdateTime(new Date());
    
    existingPolicy = scopePolicyRepo.save(existingPolicy);
    publisher.publishEvent(new ScopePolicyUpdatedEvent(this, existingPolicy));
    return existingPolicy;
  }

}
