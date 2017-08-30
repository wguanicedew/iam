package it.infn.mw.iam.persistence.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import it.infn.mw.iam.persistence.model.IamScope;

public interface IamScopeRepository extends PagingAndSortingRepository<IamScope, Long>{

}
