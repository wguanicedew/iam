package it.infn.mw.iam.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import it.infn.mw.iam.core.IamGroupRequestStatus;
import it.infn.mw.iam.persistence.model.IamGroupRequest;

public interface IamGroupRequestRepository
    extends PagingAndSortingRepository<IamGroupRequest, Long> {

  Optional<IamGroupRequest> findByUuid(@Param("uuid") String uuid);

  @Query("select r from IamGroupRequest r where r.status = it.infn.mw.iam.core.IamGroupRequestStatus.PENDING")
  List<IamGroupRequest> findPendingRequests();

  @Query("select r from IamGroupRequest r join r.account a join r.group g where a.username= :username and g.name= :groupName")
  Optional<IamGroupRequest> findByUsernameAndGroup(@Param("username") String username,
      @Param("groupName") String groupName);

  @Query("select r from IamGroupRequest r join r.account a where a.username= :username")
  Page<IamGroupRequest> findByUsername(@Param("username") String username, Pageable pageRequest);

  @Query("select r from IamGroupRequest r join r.group g where g.name= :groupName")
  Page<IamGroupRequest> findByGroup(@Param("groupName") String groupName, Pageable pageRequest);

  @Query("select r from IamGroupRequest r where r.status = :status")
  Page<IamGroupRequest> findByStatus(@Param("status") IamGroupRequestStatus status,
      Pageable pageRequest);
}
