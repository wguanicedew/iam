package it.infn.mw.iam.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import it.infn.mw.iam.persistence.model.IamGroup;
import it.infn.mw.iam.persistence.model.IamScopePolicy;

public interface IamScopePolicyRepository extends PagingAndSortingRepository<IamScopePolicy, Long> {

  @Query("select s from IamScopePolicy s where s.group is null")
  List<IamScopePolicy> findDefaultPolicies();
  
  @Query("select s from IamScopePolicy s join s.scopes ss where ss.scope = :scope")
  List<IamScopePolicy> findByScope(@Param("scope") String scope);
  
  @Query("select s from IamScopePolicy s where s.group = :group")
  List<IamScopePolicy> findByGroup(@Param("group") IamGroup group);
}
