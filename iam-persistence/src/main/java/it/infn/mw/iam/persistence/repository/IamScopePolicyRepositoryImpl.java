package it.infn.mw.iam.persistence.repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.infn.mw.iam.persistence.model.IamScopePolicy;

@Component
public class IamScopePolicyRepositoryImpl implements IamScopePolicyRepositoryCustom {

  @Autowired
  IamScopePolicyRepository repo;

  @Override
  public List<IamScopePolicy> findEquivalentPolicies(IamScopePolicy p) {

    List<IamScopePolicy> equivalentPolicies = Collections.emptyList();

    if (p.getGroup() != null) {
      equivalentPolicies = repo.findByGroupAndRule(p.getGroup(), p.getRule());
    } else if (p.getAccount() != null) {
      equivalentPolicies = repo.findByAccountAndRule(p.getAccount(), p.getRule());
    } else {
      equivalentPolicies = repo.findDefaultPoliciesByRule(p.getRule());
    }

    if (p.getScopes().isEmpty()) {
      return equivalentPolicies.stream()
        .filter(ep -> ep.getScopes().isEmpty())
        .collect(Collectors.toList());
    }

    equivalentPolicies = equivalentPolicies.stream()
      .filter(ep -> ep.getScopes().containsAll(p.getScopes()))
      .collect(Collectors.toList());

    return equivalentPolicies;
  }

}
