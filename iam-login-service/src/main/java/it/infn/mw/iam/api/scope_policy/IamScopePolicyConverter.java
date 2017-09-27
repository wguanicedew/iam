package it.infn.mw.iam.api.scope_policy;

import it.infn.mw.iam.persistence.model.IamScopePolicy;

public interface IamScopePolicyConverter {

  ScopePolicyDTO fromModel(IamScopePolicy model);
  
  IamScopePolicy toModel(ScopePolicyDTO dto);
}
