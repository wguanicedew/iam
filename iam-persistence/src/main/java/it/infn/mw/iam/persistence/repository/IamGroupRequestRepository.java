/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2018
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

  @Query("select r from IamGroupRequest r join r.account a where a.username= :username and r.status = :status")
  Page<IamGroupRequest> findByUsernameAndStatus(@Param("username") String username,
      @Param("status") IamGroupRequestStatus status, Pageable pageRequest);

  @Query("select r from IamGroupRequest r join r.group g where g.name= :groupName and r.status = :status")
  Page<IamGroupRequest> findByGroupAndStatus(@Param("groupName") String groupName,
      @Param("status") IamGroupRequestStatus status, Pageable pageRequest);
}
