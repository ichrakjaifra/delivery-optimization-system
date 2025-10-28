package com.delivery.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String licensePlate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private VehicleType type;

    @Column(nullable = false)
    private Double maxWeight; // en kg

    @Column(nullable = false)
    private Double maxVolume; // en m³

    @Column(nullable = false)
    private Integer maxDeliveries;

    @Column(nullable = false)
    private Double range; // rayon d'action en km

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Tour> tours = new ArrayList<>();

    public enum VehicleType {
        BIKE, VAN, TRUCK
    }


    public boolean isValidForDelivery(Double totalWeight, Double totalVolume, Integer deliveryCount) {
        return totalWeight <= this.maxWeight &&
                totalVolume <= this.maxVolume &&
                deliveryCount <= this.maxDeliveries;
    }


    public static class Constraints {
        public static final double BIKE_MAX_WEIGHT = 50.0; // kg
        public static final double BIKE_MAX_VOLUME = 0.5;  // m³
        public static final int BIKE_MAX_DELIVERIES = 15;

        public static final double VAN_MAX_WEIGHT = 1000.0; // kg
        public static final double VAN_MAX_VOLUME = 8.0;    // m³
        public static final int VAN_MAX_DELIVERIES = 50;

        public static final double TRUCK_MAX_WEIGHT = 5000.0; // kg
        public static final double TRUCK_MAX_VOLUME = 40.0;   // m³
        public static final int TRUCK_MAX_DELIVERIES = 100;
    }


    public void validateConstraints() {
        switch (this.type) {
            case BIKE:
                validateBikeConstraints();
                break;
            case VAN:
                validateVanConstraints();
                break;
            case TRUCK:
                validateTruckConstraints();
                break;
            default:
                throw new IllegalArgumentException("Type de véhicule non supporté: " + this.type);
        }
    }

    private void validateBikeConstraints() {
        if (this.maxWeight > Constraints.BIKE_MAX_WEIGHT) {
            throw new IllegalArgumentException("Poids max vélo cargo: " + Constraints.BIKE_MAX_WEIGHT + "kg");
        }
        if (this.maxVolume > Constraints.BIKE_MAX_VOLUME) {
            throw new IllegalArgumentException("Volume max vélo cargo: " + Constraints.BIKE_MAX_VOLUME + "m³");
        }
        if (this.maxDeliveries > Constraints.BIKE_MAX_DELIVERIES) {
            throw new IllegalArgumentException("Livraisons max vélo cargo: " + Constraints.BIKE_MAX_DELIVERIES);
        }
    }

    private void validateVanConstraints() {
        if (this.maxWeight > Constraints.VAN_MAX_WEIGHT) {
            throw new IllegalArgumentException("Poids max camionnette: " + Constraints.VAN_MAX_WEIGHT + "kg");
        }
        if (this.maxVolume > Constraints.VAN_MAX_VOLUME) {
            throw new IllegalArgumentException("Volume max camionnette: " + Constraints.VAN_MAX_VOLUME + "m³");
        }
        if (this.maxDeliveries > Constraints.VAN_MAX_DELIVERIES) {
            throw new IllegalArgumentException("Livraisons max camionnette: " + Constraints.VAN_MAX_DELIVERIES);
        }
    }

    private void validateTruckConstraints() {
        if (this.maxWeight > Constraints.TRUCK_MAX_WEIGHT) {
            throw new IllegalArgumentException("Poids max camion: " + Constraints.TRUCK_MAX_WEIGHT + "kg");
        }
        if (this.maxVolume > Constraints.TRUCK_MAX_VOLUME) {
            throw new IllegalArgumentException("Volume max camion: " + Constraints.TRUCK_MAX_VOLUME + "m³");
        }
        if (this.maxDeliveries > Constraints.TRUCK_MAX_DELIVERIES) {
            throw new IllegalArgumentException("Livraisons max camion: " + Constraints.TRUCK_MAX_DELIVERIES);
        }
    }
}