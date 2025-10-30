package com.delivery.service;

import com.delivery.entity.Delivery;
import com.delivery.repository.DeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @InjectMocks
    private DeliveryService deliveryService;

    private Delivery delivery;
    private Delivery delivery2;

    @BeforeEach
    void setUp() {
        delivery = new Delivery();
        delivery.setId(1L);
        delivery.setAddress("123 Rue Test, Casablanca");
        delivery.setLatitude(33.5731);
        delivery.setLongitude(-7.5898);
        delivery.setWeight(5.0);
        delivery.setVolume(0.5);
        delivery.setStatus(Delivery.DeliveryStatus.PENDING);

        delivery2 = new Delivery();
        delivery2.setId(2L);
        delivery2.setAddress("456 Avenue Test, Rabat");
        delivery2.setLatitude(34.0209);
        delivery2.setLongitude(-6.8416);
        delivery2.setWeight(10.0);
        delivery2.setVolume(1.0);
        delivery2.setStatus(Delivery.DeliveryStatus.PENDING);
    }

    @Test
    void getAllDeliveries_ShouldReturnAllDeliveries() {
        // Arrange
        List<Delivery> expectedDeliveries = Arrays.asList(delivery, delivery2);
        when(deliveryRepository.findAll()).thenReturn(expectedDeliveries);

        // Act
        List<Delivery> result = deliveryService.getAllDeliveries();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(deliveryRepository, times(1)).findAll();
    }

    @Test
    void getDeliveryById_WithValidId_ShouldReturnDelivery() {
        // Arrange
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));

        // Act
        Optional<Delivery> result = deliveryService.getDeliveryById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(delivery.getId(), result.get().getId());
        assertEquals(delivery.getAddress(), result.get().getAddress());
    }

    @Test
    void getDeliveryById_WithInvalidId_ShouldReturnEmpty() {
        // Arrange
        when(deliveryRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Delivery> result = deliveryService.getDeliveryById(99L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void createDelivery_WithValidData_ShouldSaveAndReturnDelivery() {
        // Arrange
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);

        // Act
        Delivery result = deliveryService.createDelivery(delivery);

        // Assert
        assertNotNull(result);
        assertEquals(delivery.getId(), result.getId());
        verify(deliveryRepository, times(1)).save(delivery);
    }

    @Test
    void createDelivery_WithInvalidWeight_ShouldThrowException() {
        // Arrange
        delivery.setWeight(-5.0);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            deliveryService.createDelivery(delivery);
        });

        assertTrue(exception.getMessage().contains("Erreur de validation"));
        verify(deliveryRepository, never()).save(any());
    }

    @Test
    void updateDelivery_WithValidId_ShouldUpdateAndReturnDelivery() {
        // Arrange
        Delivery updatedDelivery = new Delivery();
        updatedDelivery.setAddress("789 Nouvelle Adresse");
        updatedDelivery.setLatitude(33.5731);
        updatedDelivery.setLongitude(-7.5898);
        updatedDelivery.setWeight(8.0);
        updatedDelivery.setVolume(0.8);

        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(updatedDelivery);

        // Act
        Delivery result = deliveryService.updateDelivery(1L, updatedDelivery);

        // Assert
        assertNotNull(result);
        assertEquals("789 Nouvelle Adresse", result.getAddress());
        verify(deliveryRepository, times(1)).findById(1L);
        verify(deliveryRepository, times(1)).save(delivery);
    }

    @Test
    void updateDelivery_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(deliveryRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            deliveryService.updateDelivery(99L, delivery);
        });

        assertEquals("Delivery not found with id: 99", exception.getMessage());
    }

    @Test
    void deleteDelivery_WithValidId_ShouldDeleteDelivery() {
        // Arrange
        when(deliveryRepository.existsById(1L)).thenReturn(true);

        // Act
        deliveryService.deleteDelivery(1L);

        // Assert
        verify(deliveryRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteDelivery_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(deliveryRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            deliveryService.deleteDelivery(99L);
        });

        assertEquals("Delivery not found with id: 99", exception.getMessage());
    }

    @Test
    void getDeliveriesByStatus_ShouldReturnFilteredDeliveries() {
        // Arrange
        List<Delivery> pendingDeliveries = Arrays.asList(delivery, delivery2);
        when(deliveryRepository.findByStatus(Delivery.DeliveryStatus.PENDING)).thenReturn(pendingDeliveries);

        // Act
        List<Delivery> result = deliveryService.getDeliveriesByStatus(Delivery.DeliveryStatus.PENDING);

        // Assert
        assertEquals(2, result.size());
        verify(deliveryRepository, times(1)).findByStatus(Delivery.DeliveryStatus.PENDING);
    }

    @Test
    void getUnassignedDeliveries_ShouldReturnUnassignedDeliveries() {
        // Arrange
        List<Delivery> unassignedDeliveries = Arrays.asList(delivery);
        when(deliveryRepository.findUnassignedDeliveries()).thenReturn(unassignedDeliveries);

        // Act
        List<Delivery> result = deliveryService.getUnassignedDeliveries();

        // Assert
        assertEquals(1, result.size());
        verify(deliveryRepository, times(1)).findUnassignedDeliveries();
    }
}