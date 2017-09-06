package it.infn.mw.iam.api.scope_policy;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.infn.mw.iam.persistence.model.IamScopePolicy;
import it.infn.mw.iam.persistence.repository.IamScopePolicyRepository;
import it.infn.mw.iam.persistence.repository.IamScopeRepository;

@Service
public class DefaultScopePolicyService implements ScopePolicyService {

  private final IamScopeRepository scopeRepo;
  private final IamScopePolicyRepository scopePolicyRepo;
  private final IamScopePolicyConverter converter;

  @Autowired
  public DefaultScopePolicyService(IamScopeRepository scopeRepo,
      IamScopePolicyRepository scopePolicyRepo, IamScopePolicyConverter converter) {
    this.scopeRepo = scopeRepo;
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
        scopePolicyRepo.findById(policyId).orElseThrow(() -> new ScopePolicyNotFoundError(
            String.format("No scope policy found for id: %d", policyId)));

    scopePolicyRepo.delete(sp);
  }

  @Override
  public IamScopePolicy createScopePolicy(ScopePolicyDTO scopePolicy) {
    IamScopePolicy sp = converter.toModel(scopePolicy);
    Date now = new Date();
    sp.setCreationTime(now);
    sp.setLastUpdateTime(now);
    return scopePolicyRepo.save(sp);

  }

}
