package it.infn.mw.iam.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import it.infn.mw.iam.persistence.model.IamGroupRequest;
import it.infn.mw.iam.persistence.model.IamRegistrationRequest;

public interface IamGroupRequestRepository
    extends PagingAndSortingRepository<IamGroupRequest, Long> {

  Optional<IamGroupRequest> findByUuid(@Param("uuid") String uuid);

  @Query("select r from IamGroupRequest r where r.status = it.infn.mw.iam.core.IamGroupRequestStatus.PENDING")
  List<IamGroupRequest> findPendingRequests();

  @Query("select r from IamGroupRequest r join r.account a where a.uuid = :accountUuid")
  Optional<IamRegistrationRequest> findByAccountUuid(@Param("accountUuid") String accountUuid);

  @Query("select r from IamGroupRequest r join r.account a where a.username= :username")
  Optional<IamRegistrationRequest> findByUsername(@Param("username") String username);

  @Query("select r from IamGroupRequest r join r.account a join r.group g where a.username= :username and g.name= :groupName")
  Optional<IamGroupRequest> findByUsernameAndGroup(@Param("username") String username,
      @Param("groupName") String groupName);
}
