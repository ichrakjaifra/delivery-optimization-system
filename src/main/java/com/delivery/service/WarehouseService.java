package com.delivery.service;

import com.delivery.entity.Warehouse;
import com.delivery.repository.WarehouseRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class WarehouseService {

    private static final Logger logger = Logger.getLogger(WarehouseService.class.getName());

    private final WarehouseRepository warehouseRepository;

    public WarehouseService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    public List<Warehouse> getAllWarehouses() {
        logger.info("Fetching all warehouses");
        return warehouseRepository.findAll();
    }

    public Optional<Warehouse> getWarehouseById(Long id) {
        logger.info("Fetching warehouse with id: " + id);
        return warehouseRepository.findById(id);
    }

    @Transactional
    public Warehouse createWarehouse(Warehouse warehouse) {
        logger.info("Creating new warehouse: " + warehouse.getName());

        try {
            warehouse.validate();
            logger.info("Contraintes validées pour l'entrepôt: " + warehouse.getName());
        } catch (IllegalArgumentException e) {
            logger.severe("Erreur validation entrepôt: " + e.getMessage());
            throw new RuntimeException("Erreur de validation: " + e.getMessage());
        }

        return warehouseRepository.save(warehouse);
    }

    @Transactional
    public Warehouse updateWarehouse(Long id, Warehouse warehouseDetails) {
        logger.info("Updating warehouse with id: " + id);

        try {
            warehouseDetails.validate();
            logger.info("Contraintes validées pour la mise à jour entrepôt: " + warehouseDetails.getName());
        } catch (IllegalArgumentException e) {
            logger.severe("Erreur validation entrepôt: " + e.getMessage());
            throw new RuntimeException("Erreur de validation: " + e.getMessage());
        }

        Optional<Warehouse> warehouseOpt = warehouseRepository.findById(id);
        if (warehouseOpt.isPresent()) {
            Warehouse warehouse = warehouseOpt.get();
            warehouse.setName(warehouseDetails.getName());
            warehouse.setAddress(warehouseDetails.getAddress());
            warehouse.setLatitude(warehouseDetails.getLatitude());
            warehouse.setLongitude(warehouseDetails.getLongitude());
            warehouse.setOpeningHours(warehouseDetails.getOpeningHours());
            return warehouseRepository.save(warehouse);
        }
        throw new RuntimeException("Warehouse not found with id: " + id);
    }

    @Transactional
    public void deleteWarehouse(Long id) {
        logger.info("Deleting warehouse with id: " + id);
        if (warehouseRepository.existsById(id)) {
            warehouseRepository.deleteById(id);
        } else {
            throw new RuntimeException("Warehouse not found with id: " + id);
        }
    }

    public List<Warehouse> getActiveWarehouses() {
        logger.info("Fetching active warehouses");
        return warehouseRepository.findActiveWarehouses();
    }

    public Warehouse getWarehouseByName(String name) {
        logger.info("Fetching warehouse by name: " + name);
        return warehouseRepository.findByName(name);
    }
}