package com.delivery.mapper;

import com.delivery.dto.TourDTO;
import com.delivery.entity.Tour;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TourMapper {

    public TourDTO toDTO(Tour tour) {
        if (tour == null) {
            return null;
        }

        TourDTO dto = new TourDTO();
        dto.setId(tour.getId());
        dto.setDate(tour.getDate());
        dto.setVehicleId(tour.getVehicle() != null ? tour.getVehicle().getId() : null);
        dto.setWarehouseId(tour.getWarehouse() != null ? tour.getWarehouse().getId() : null);
        dto.setAlgorithmUsed(tour.getAlgorithmUsed());
        dto.setTotalDistance(tour.getTotalDistance());
        dto.setDeliveryIds(tour.getDeliveries().stream()
                .map(delivery -> delivery.getId())
                .collect(Collectors.toList()));

        return dto;
    }

    public Tour toEntity(TourDTO dto) {
        if (dto == null) {
            return null;
        }

        Tour tour = new Tour();
        tour.setId(dto.getId());
        tour.setDate(dto.getDate());
        tour.setAlgorithmUsed(dto.getAlgorithmUsed());
        tour.setTotalDistance(dto.getTotalDistance());

        return tour;
    }
}