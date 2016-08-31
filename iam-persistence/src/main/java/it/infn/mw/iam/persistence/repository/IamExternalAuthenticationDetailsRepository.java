package it.infn.mw.iam.persistence.repository;

import java.util.Optional;

import org.mitre.oauth2.model.AuthenticationHolderEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import it.infn.mw.iam.persistence.model.IamExternalAuthenticationDetails;

public interface IamExternalAuthenticationDetailsRepository
    extends PagingAndSortingRepository<IamExternalAuthenticationDetails, Long> {

  Optional<IamExternalAuthenticationDetails> findByHolder(
      @Param("holder") AuthenticationHolderEntity holder);


}
