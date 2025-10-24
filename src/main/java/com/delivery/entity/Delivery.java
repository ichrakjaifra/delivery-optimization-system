package com.delivery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Entity
@Table(name = "deliveries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String address;

    @Column(nullable = false)
    private Double latitude; // Coordonnée GPS

    @Column(nullable = false)
    private Double longitude; // Coordonnée GPS

    @Column(nullable = false)
    private Double weight; // en kg

    @Column(nullable = false)
    private Double volume; // en m³

    @Column(name = "preferred_time_slot", length = 20)
    private String preferredTimeSlot; // Format: "09:00-11:00"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private DeliveryStatus status = DeliveryStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id")
    private Tour tour;

    @Column(name = "delivery_order")
    private Integer order; // Ordre dans la tournée

    public enum DeliveryStatus {
        PENDING, IN_TRANSIT, DELIVERED, FAILED
    }


    private static final Pattern TIME_SLOT_PATTERN =
            Pattern.compile("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]-([0-1]?[0-9]|2[0-3]):[0-5][0-9]$");


    public void validate() {
        validateWeight();
        validateVolume();
        validateTimeSlot();
        validateCoordinates();
    }

    private void validateWeight() {
        if (this.weight == null || this.weight <= 0) {
            throw new IllegalArgumentException("Le poids doit être positif");
        }
        if (this.weight > 1000) { // Contrainte réaliste
            throw new IllegalArgumentException("Le poids ne peut pas dépasser 1000kg");
        }
    }

    private void validateVolume() {
        if (this.volume == null || this.volume <= 0) {
            throw new IllegalArgumentException("Le volume doit être positif");
        }
        if (this.volume > 10) { // Contrainte réaliste
            throw new IllegalArgumentException("Le volume ne peut pas dépasser 10m³");
        }
    }

    private void validateTimeSlot() {
        if (this.preferredTimeSlot != null && !this.preferredTimeSlot.isEmpty()) {
            if (!TIME_SLOT_PATTERN.matcher(this.preferredTimeSlot).matches()) {
                throw new IllegalArgumentException("Format de créneau horaire invalide. Utilisez: HH:MM-HH:MM");
            }
        }
    }

    private void validateCoordinates() {
        if (this.latitude == null || this.latitude < -90 || this.latitude > 90) {
            throw new IllegalArgumentException("Latitude invalide (-90 à 90)");
        }
        if (this.longitude == null || this.longitude < -180 || this.longitude > 180) {
            throw new IllegalArgumentException("Longitude invalide (-180 à 180)");
        }
    }


    /*public double calculateDistance(Delivery other) {
        final int R = 6371;

        double latDistance = Math.toRadians(other.latitude - this.latitude);
        double lonDistance = Math.toRadians(other.longitude - this.longitude);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(other.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }*/

    /*public double calculateDistance(Warehouse warehouse) {
        final int R = 6371;

        double latDistance = Math.toRadians(warehouse.getLatitude() - this.latitude);
        double lonDistance = Math.toRadians(warehouse.getLongitude() - this.longitude);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(warehouse.getLatitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }*/
}