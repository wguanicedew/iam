package it.infn.mw.iam.api.scope_policy;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.infn.mw.iam.persistence.model.IamScopePolicy;
import it.infn.mw.iam.persistence.repository.IamScopePolicyRepository;

@Service
public class DefaultScopePolicyService implements ScopePolicyService {

  private final IamScopePolicyRepository scopePolicyRepo;
  private final IamScopePolicyConverter converter;

  private ScopePolicyNotFoundError notFoundError(Long id) {
    return new ScopePolicyNotFoundError(String.format("No scope policy found for id: %d", id));
  }

  @Autowired
  public DefaultScopePolicyService(IamScopePolicyRepository scopePolicyRepo,
      IamScopePolicyConverter converter) {
    this.scopePolicyRepo = scopePolicyRepo;
    this.converter = converter;
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

    return scopePolicyRepo.save(sp);

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
    
    return scopePolicyRepo.save(existingPolicy);
  }

}
