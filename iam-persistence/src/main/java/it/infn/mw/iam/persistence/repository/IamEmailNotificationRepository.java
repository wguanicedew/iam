package it.infn.mw.iam.persistence.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import it.infn.mw.iam.core.IamDeliveryStatus;
import it.infn.mw.iam.persistence.model.IamEmailNotification;

public interface IamEmailNotificationRepository
    extends PagingAndSortingRepository<IamEmailNotification, Long> {

  List<IamEmailNotification> findByDeliveryStatus(
      @Param("delivery_status") IamDeliveryStatus deliveryStatus);
}
