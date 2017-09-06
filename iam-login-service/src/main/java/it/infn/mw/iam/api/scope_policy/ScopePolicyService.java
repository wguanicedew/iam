package it.infn.mw.iam.api.scope_policy;

import java.util.Optional;

import it.infn.mw.iam.persistence.model.IamScopePolicy;

public interface ScopePolicyService {
  
  Iterable<IamScopePolicy> findAllScopePolicies();
  
  Optional<IamScopePolicy> findScopePolicyById(Long policyId);
  
  void deleteScopePolicyById(Long policyId);
  
  IamScopePolicy createScopePolicy(ScopePolicyDTO scopePolicy);

}
