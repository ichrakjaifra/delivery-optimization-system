package com.delivery.dto;

import com.delivery.entity.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {
    private Long id;
    private String licensePlate;
    private Vehicle.VehicleType type;
    private Double maxWeight;
    private Double maxVolume;
    private Integer maxDeliveries;
    private Double range;
}