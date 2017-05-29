package it.infn.mw.iam.persistence.repository;

import java.util.Date;
import java.util.List;

import org.mitre.oauth2.model.OAuth2RefreshTokenEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface IamOAuthRefreshTokenRepository
    extends PagingAndSortingRepository<OAuth2RefreshTokenEntity, Long> {

  @Query("select t from OAuth2RefreshTokenEntity t where t.authenticationHolder.userAuth.name = :userId "
      + "and (t.expiration is NULL or t.expiration > :timestamp)")
  List<OAuth2RefreshTokenEntity> findValidRefreshTokensForUser(@Param("userId") String userId,
      @Param("timestamp") Date timestamp);
}
