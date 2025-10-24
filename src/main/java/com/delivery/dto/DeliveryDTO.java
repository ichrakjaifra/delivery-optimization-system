package com.delivery.dto;

import com.delivery.entity.Delivery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDTO {
    private Long id;
    private String address;
    private Double latitude;
    private Double longitude;
    private Double weight;
    private Double volume;
    private String preferredTimeSlot;
    private Delivery.DeliveryStatus status;
    private Long tourId;
    private Integer order;
}