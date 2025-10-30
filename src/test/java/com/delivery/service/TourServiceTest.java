package com.delivery.service;

import com.delivery.entity.*;
import com.delivery.optimizer.TourOptimizer;
import com.delivery.repository.TourRepository;
import com.delivery.repository.DeliveryRepository;
import com.delivery.repository.VehicleRepository;
import com.delivery.repository.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourServiceTest {

    @Mock
    private TourRepository tourRepository;

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private TourOptimizer nearestNeighborOptimizer;

    @Mock
    private TourOptimizer clarkeWrightOptimizer;


    private TourService tourService;

    private Tour tour;
    private Vehicle vehicle;
    private Warehouse warehouse;
    private Delivery delivery1;
    private Delivery delivery2;

    @BeforeEach
    void setUp() {
        this.tourService = new TourService(
                tourRepository,
                deliveryRepository,
                vehicleRepository,
                warehouseRepository,
                nearestNeighborOptimizer,
                clarkeWrightOptimizer
        );

        // Setup Vehicle
        vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setLicensePlate("ABC123");
        vehicle.setType(Vehicle.VehicleType.VAN);
        vehicle.setMaxWeight(1000.0);
        vehicle.setMaxVolume(8.0);
        vehicle.setMaxDeliveries(50);
        vehicle.setRange(500.0);

        // Setup Warehouse
        warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Entrepôt Principal");
        warehouse.setAddress("123 Rue Entrepôt, Casablanca");
        warehouse.setLatitude(33.5731);
        warehouse.setLongitude(-7.5898);
        warehouse.setOpeningHours("06:00-22:00");

        // Setup Deliveries
        delivery1 = new Delivery();
        delivery1.setId(1L);
        delivery1.setAddress("123 Rue Test 1");
        delivery1.setLatitude(33.5741);
        delivery1.setLongitude(-7.5908);
        delivery1.setWeight(5.0);
        delivery1.setVolume(0.5);

        delivery2 = new Delivery();
        delivery2.setId(2L);
        delivery2.setAddress("456 Rue Test 2");
        delivery2.setLatitude(33.5751);
        delivery2.setLongitude(-7.5918);
        delivery2.setWeight(10.0);
        delivery2.setVolume(1.0);

        // Setup Tour - utiliser ArrayList mutable
        tour = new Tour();
        tour.setId(1L);
        tour.setDate(LocalDate.now());
        tour.setVehicle(vehicle);
        tour.setWarehouse(warehouse);
        tour.setAlgorithmUsed(Tour.AlgorithmType.NEAREST_NEIGHBOR);
        tour.setTotalDistance(50.0);
        tour.setDeliveries(new ArrayList<>(Arrays.asList(delivery1, delivery2)));
    }

    @Test
    void getAllTours_ShouldReturnAllTours() {
        // Arrange
        List<Tour> expectedTours = Arrays.asList(tour);
        when(tourRepository.findAll()).thenReturn(expectedTours);

        // Act
        List<Tour> result = tourService.getAllTours();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(tourRepository, times(1)).findAll();
    }

    @Test
    void getTourById_WithValidId_ShouldReturnTour() {
        // Arrange
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));

        // Act
        Optional<Tour> result = tourService.getTourById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(tour.getId(), result.get().getId());
    }

    @Test
    void createTour_WithValidData_ShouldSaveAndReturnTour() {
        // Arrange
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(tourRepository.save(any(Tour.class))).thenReturn(tour);

        // Act
        Tour result = tourService.createTour(tour);

        // Assert
        assertNotNull(result);
        assertEquals(tour.getId(), result.getId());
        verify(tourRepository, times(1)).save(tour);
    }

    @Test
    void createTour_WithInvalidVehicle_ShouldThrowException() {
        // Arrange
        // Créer un nouveau véhicule avec un ID non existant
        Vehicle invalidVehicle = new Vehicle();
        invalidVehicle.setId(99L);
        tour.setVehicle(invalidVehicle);

        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tourService.createTour(tour);
        });

        assertTrue(exception.getMessage().contains("Vehicle not found"));
        verify(tourRepository, never()).save(any(Tour.class));
    }

    @Test
    void createTour_WithInvalidWarehouse_ShouldThrowException() {
        // Arrange
        Warehouse invalidWarehouse = new Warehouse();
        invalidWarehouse.setId(99L);
        tour.setWarehouse(invalidWarehouse);

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(warehouseRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tourService.createTour(tour);
        });

        assertTrue(exception.getMessage().contains("Warehouse not found"));
        verify(tourRepository, never()).save(any(Tour.class));
    }

    @Test
    void optimizeTour_WithNearestNeighbor_ShouldOptimizeTour() {
        // Arrange
        List<Delivery> optimizedDeliveries = new ArrayList<>(Arrays.asList(delivery1, delivery2));
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(nearestNeighborOptimizer.calculateOptimalTour(
                eq(warehouse),
                any(List.class),
                eq(vehicle)
        )).thenReturn(optimizedDeliveries);
        when(nearestNeighborOptimizer.calculateTotalDistance(eq(warehouse), any(List.class))).thenReturn(45.0);
        when(tourRepository.save(any(Tour.class))).thenReturn(tour);

        // Act
        Tour result = tourService.optimizeTour(1L, Tour.AlgorithmType.NEAREST_NEIGHBOR);

        // Assert
        assertNotNull(result);
        verify(nearestNeighborOptimizer, times(1)).calculateOptimalTour(
                eq(warehouse),
                any(List.class),
                eq(vehicle)
        );
        verify(tourRepository, times(1)).save(tour);
    }

    @Test
    void optimizeTour_WithClarkeWright_ShouldOptimizeTour() {
        // Arrange
        tour.setAlgorithmUsed(Tour.AlgorithmType.CLARKE_WRIGHT);
        List<Delivery> optimizedDeliveries = new ArrayList<>(Arrays.asList(delivery1, delivery2));
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(clarkeWrightOptimizer.calculateOptimalTour(
                eq(warehouse),
                any(List.class),
                eq(vehicle)
        )).thenReturn(optimizedDeliveries);
        when(clarkeWrightOptimizer.calculateTotalDistance(eq(warehouse), any(List.class))).thenReturn(40.0);
        when(tourRepository.save(any(Tour.class))).thenReturn(tour);

        // Act
        Tour result = tourService.optimizeTour(1L, Tour.AlgorithmType.CLARKE_WRIGHT);

        // Assert
        assertNotNull(result);
        verify(clarkeWrightOptimizer, times(1)).calculateOptimalTour(
                eq(warehouse),
                any(List.class),
                eq(vehicle)
        );
        verify(tourRepository, times(1)).save(tour);
    }

    @Test
    void optimizeTour_WithVehicleCapacityExceeded_ShouldThrowException() {
        // Arrange
        delivery1.setWeight(2000.0); // Exceeds vehicle capacity
        tour.setDeliveries(new ArrayList<>(Arrays.asList(delivery1, delivery2)));

        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tourService.optimizeTour(1L, Tour.AlgorithmType.NEAREST_NEIGHBOR);
        });

        assertTrue(exception.getMessage().contains("ne peut pas transporter"));
        verify(tourRepository, never()).save(any(Tour.class));
    }

    @Test
    void optimizeTour_WithNoDeliveries_ShouldThrowException() {
        // Arrange
        tour.setDeliveries(new ArrayList<>()); // Empty deliveries
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tourService.optimizeTour(1L, Tour.AlgorithmType.NEAREST_NEIGHBOR);
        });

        assertTrue(exception.getMessage().contains("No deliveries found"));
        verify(tourRepository, never()).save(any(Tour.class));
    }

    @Test
    void addDeliveryToTour_WithValidIds_ShouldAddDelivery() {
        // Arrange
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(deliveryRepository.findById(3L)).thenReturn(Optional.of(delivery1));

        // Act
        tourService.addDeliveryToTour(1L, 3L);

        // Assert
        verify(deliveryRepository, times(1)).save(delivery1);
        assertEquals(tour, delivery1.getTour());
    }

    @Test
    void removeDeliveryFromTour_WithValidIds_ShouldRemoveDelivery() {
        // Arrange
        delivery1.setTour(tour);
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery1));

        // Act
        tourService.removeDeliveryFromTour(1L, 1L);

        // Assert
        assertNull(delivery1.getTour());
        assertNull(delivery1.getOrder());
        verify(deliveryRepository, times(1)).save(delivery1);
    }

    @Test
    void getToursByDate_ShouldReturnToursForDate() {
        // Arrange
        LocalDate date = LocalDate.now();
        List<Tour> expectedTours = Arrays.asList(tour);
        when(tourRepository.findByDate(date)).thenReturn(expectedTours);

        // Act
        List<Tour> result = tourService.getToursByDate(date);

        // Assert
        assertEquals(1, result.size());
        verify(tourRepository, times(1)).findByDate(date);
    }

    @Test
    void deleteTour_ShouldRemoveTourAndUnassignDeliveries() {
        // Arrange
        delivery1.setTour(tour);
        delivery2.setTour(tour);

        // Utiliser ArrayList mutable
        List<Delivery> deliveries = new ArrayList<>(Arrays.asList(delivery1, delivery2));
        tour.setDeliveries(deliveries);

        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));

        // Act
        tourService.deleteTour(1L);

        // Assert
        assertNull(delivery1.getTour());
        assertNull(delivery1.getOrder());
        assertNull(delivery2.getTour());
        assertNull(delivery2.getOrder());
        verify(deliveryRepository, times(2)).save(any(Delivery.class));
        verify(tourRepository, times(1)).delete(tour);
    }

    @Test
    void updateTour_WithValidId_ShouldUpdateTour() {
        // Arrange
        Tour updatedTour = new Tour();
        updatedTour.setDate(LocalDate.now().plusDays(1));
        updatedTour.setAlgorithmUsed(Tour.AlgorithmType.CLARKE_WRIGHT);
        updatedTour.setTotalDistance(60.0);

        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(tourRepository.save(any(Tour.class))).thenReturn(updatedTour);

        // Act
        Tour result = tourService.updateTour(1L, updatedTour);

        // Assert
        assertNotNull(result);
        verify(tourRepository, times(1)).save(tour);
    }

    @Test
    void getToursWithNearestNeighbor_ShouldReturnTours() {
        // Arrange
        List<Tour> expectedTours = Arrays.asList(tour);
        when(tourRepository.findToursWithNearestNeighbor()).thenReturn(expectedTours);

        // Act
        List<Tour> result = tourService.getToursWithNearestNeighbor();

        // Assert
        assertEquals(1, result.size());
        verify(tourRepository, times(1)).findToursWithNearestNeighbor();
    }

    @Test
    void getToursWithClarkeWright_ShouldReturnTours() {
        // Arrange
        List<Tour> expectedTours = Arrays.asList(tour);
        when(tourRepository.findToursWithClarkeWright()).thenReturn(expectedTours);

        // Act
        List<Tour> result = tourService.getToursWithClarkeWright();

        // Assert
        assertEquals(1, result.size());
        verify(tourRepository, times(1)).findToursWithClarkeWright();
    }
}