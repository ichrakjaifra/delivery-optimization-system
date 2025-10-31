package com.delivery.repository;

import com.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    List<Delivery> findByStatus(Delivery.DeliveryStatus status);

    List<Delivery> findByTourIdOrderByOrderAsc(Long tourId);

    @Query("SELECT d FROM Delivery d WHERE d.tour IS NULL")
    List<Delivery> findUnassignedDeliveries();

    @Query("SELECT d FROM Delivery d WHERE d.weight > :minWeight")
    List<Delivery> findHeavyDeliveries(@Param("minWeight") Double minWeight);

    @Query("SELECT d FROM Delivery d WHERE d.tour IS NULL AND d.status = 'PENDING'")
    List<Delivery> findPendingUnassignedDeliveries();
}