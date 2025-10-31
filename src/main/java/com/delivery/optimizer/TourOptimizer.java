package com.delivery.optimizer;

import com.delivery.entity.Delivery;
import com.delivery.entity.Warehouse;
import com.delivery.entity.Vehicle;

import java.util.List;

public interface TourOptimizer {
    List<Delivery> calculateOptimalTour(Warehouse warehouse, List<Delivery> deliveries, Vehicle vehicle);
    Double calculateTotalDistance(Warehouse warehouse, List<Delivery> deliveries);
}