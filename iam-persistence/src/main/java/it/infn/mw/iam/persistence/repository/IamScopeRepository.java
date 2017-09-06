package it.infn.mw.iam.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import it.infn.mw.iam.persistence.model.IamScope;

public interface IamScopeRepository extends PagingAndSortingRepository<IamScope, Long>{
  
  Optional<IamScope> findByScope(String scope);

  @Query("select s from IamScope s where s.policies is EMPTY")
  List<IamScope> findUnlinkedScopes();
}
