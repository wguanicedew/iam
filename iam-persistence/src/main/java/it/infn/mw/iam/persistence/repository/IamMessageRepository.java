package it.infn.mw.iam.persistence.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import it.infn.mw.iam.core.IamMessageStatus;
import it.infn.mw.iam.persistence.model.IamMessage;

public interface IamMessageRepository extends PagingAndSortingRepository<IamMessage, Long> {

  List<IamMessage> findByStatus(@Param("status") IamMessageStatus messageStatus);
}
