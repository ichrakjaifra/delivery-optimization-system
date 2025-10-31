package com.delivery.repository;

import com.delivery.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByType(Vehicle.VehicleType type);

    @Query("SELECT v FROM Vehicle v WHERE v.id NOT IN (SELECT t.vehicle.id FROM Tour t WHERE t.date = CURRENT_DATE)")
    List<Vehicle> findAvailableVehicles();

    @Query("SELECT v FROM Vehicle v WHERE v.maxWeight >= :requiredWeight AND v.maxVolume >= :requiredVolume")
    List<Vehicle> findSuitableVehicles(@Param("requiredWeight") Double requiredWeight,
                                       @Param("requiredVolume") Double requiredVolume);

    Vehicle findByLicensePlate(String licensePlate);
}