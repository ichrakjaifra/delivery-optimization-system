package com.delivery.service;

import com.delivery.entity.*;

import com.delivery.repository.TourRepository;
import com.delivery.repository.DeliveryRepository;
import com.delivery.repository.VehicleRepository;
import com.delivery.repository.WarehouseRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class TourService {

    private static final Logger logger = Logger.getLogger(TourService.class.getName());

    private final TourRepository tourRepository;
    private final DeliveryRepository deliveryRepository;
    private final VehicleRepository vehicleRepository;
    private final WarehouseRepository warehouseRepository;


    public TourService(TourRepository tourRepository, DeliveryRepository deliveryRepository,
                       VehicleRepository vehicleRepository, WarehouseRepository warehouseRepository) {
        this.tourRepository = tourRepository;
        this.deliveryRepository = deliveryRepository;
        this.vehicleRepository = vehicleRepository;
        this.warehouseRepository = warehouseRepository;


    }

    public List<Tour> getAllTours() {
        logger.info("Fetching all tours");
        return tourRepository.findAll();
    }

    public Optional<Tour> getTourById(Long id) {
        logger.info("Fetching tour with id: " + id);
        return tourRepository.findById(id);
    }

    @Transactional
    public Tour createTour(Tour tour) {
        logger.info("Creating new tour for date: " + tour.getDate());

        // Vérifier si le véhicule existe
        if (tour.getVehicle() != null && tour.getVehicle().getId() != null) {
            Vehicle vehicle = vehicleRepository.findById(tour.getVehicle().getId())
                    .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + tour.getVehicle().getId()));
            tour.setVehicle(vehicle);
        }

        // Vérifier si l'entrepôt existe
        if (tour.getWarehouse() != null && tour.getWarehouse().getId() != null) {
            Warehouse warehouse = warehouseRepository.findById(tour.getWarehouse().getId())
                    .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + tour.getWarehouse().getId()));
            tour.setWarehouse(warehouse);
        }

        try {
            tour.validate();
            logger.info("Contraintes validées pour la tournée du: " + tour.getDate());
        } catch (IllegalArgumentException e) {
            logger.severe("Erreur validation tournée: " + e.getMessage());
            throw new RuntimeException("Erreur de validation: " + e.getMessage());
        }

        return tourRepository.save(tour);
    }



    public List<Tour> getToursByDate(LocalDate date) {
        logger.info("Fetching tours for date: " + date);
        return tourRepository.findByDate(date);
    }

    public List<Tour> getToursByVehicle(Long vehicleId) {
        logger.info("Fetching tours for vehicle id: " + vehicleId);
        return tourRepository.findByVehicleId(vehicleId);
    }

    public List<Tour> getToursWithNearestNeighbor() {
        logger.info("Fetching tours optimized with Nearest Neighbor");
        return tourRepository.findToursWithNearestNeighbor();
    }

    public List<Tour> getToursWithClarkeWright() {
        logger.info("Fetching tours optimized with Clarke & Wright");
        return tourRepository.findToursWithClarkeWright();
    }

    @Transactional
    public void addDeliveryToTour(Long tourId, Long deliveryId) {
        logger.info("Adding delivery " + deliveryId + " to tour " + tourId);

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour not found with id: " + tourId));

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found with id: " + deliveryId));

        delivery.setTour(tour);
        deliveryRepository.save(delivery);
    }

    @Transactional
    public void removeDeliveryFromTour(Long tourId, Long deliveryId) {
        logger.info("Removing delivery " + deliveryId + " from tour " + tourId);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found with id: " + deliveryId));

        if (delivery.getTour() == null || !delivery.getTour().getId().equals(tourId)) {
            throw new RuntimeException("Delivery " + deliveryId + " is not assigned to tour " + tourId);
        }

        delivery.setTour(null);
        delivery.setOrder(null);
        deliveryRepository.save(delivery);
    }
}