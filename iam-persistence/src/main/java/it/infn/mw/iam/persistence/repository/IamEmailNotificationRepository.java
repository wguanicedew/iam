package it.infn.mw.iam.persistence.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import it.infn.mw.iam.core.IamDeliveryStatus;
import it.infn.mw.iam.persistence.model.IamEmailNotification;

public interface IamEmailNotificationRepository
    extends PagingAndSortingRepository<IamEmailNotification, Long> {

  List<IamEmailNotification> findByDeliveryStatus(
      @Param("delivery_status") IamDeliveryStatus deliveryStatus);

  @Query("select n from IamEmailNotification n where n.deliveryStatus = :delivery_status and n.lastUpdate < :last_update")
  List<IamEmailNotification> findByStatusWithUpdateTime(
      @Param("delivery_status") IamDeliveryStatus deliveryStatus,
      @Param("last_update") Date lastUpdate);
}
