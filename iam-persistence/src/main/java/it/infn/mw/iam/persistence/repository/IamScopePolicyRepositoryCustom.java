package it.infn.mw.iam.persistence.repository;

import java.util.List;

import it.infn.mw.iam.persistence.model.IamScopePolicy;

@FunctionalInterface
public interface IamScopePolicyRepositoryCustom {
  
  List<IamScopePolicy> findEquivalentPolicies(IamScopePolicy p);

}
