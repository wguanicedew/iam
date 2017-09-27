package it.infn.mw.iam.api.scope_policy;

import java.util.List;
import java.util.stream.Collectors;

import it.infn.mw.iam.persistence.model.IamScopePolicy;

public class DuplicateScopePolicyError extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public static final String MSG_TEMPLATE =
      "Duplicate policy error: found equivalent policies in repository with ids: %s";


  public DuplicateScopePolicyError(List<IamScopePolicy> equivalentPolicies) {
    super(String.format(MSG_TEMPLATE, equivalentPolicies.stream()
      .map(IamScopePolicy::getId)
      .map(Object::toString)
      .collect(Collectors.joining(","))));
  }

}
