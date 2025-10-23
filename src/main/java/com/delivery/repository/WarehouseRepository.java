package com.delivery.repository;

import com.delivery.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    @Query("SELECT w FROM Warehouse w WHERE w.openingHours IS NOT NULL")
    List<Warehouse> findActiveWarehouses();

    Warehouse findByName(String name);
}