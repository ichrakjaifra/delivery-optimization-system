package com.delivery.controller;

import com.delivery.dto.TourDTO;
import com.delivery.entity.Delivery;
import com.delivery.entity.Tour;
import com.delivery.mapper.TourMapper;
import com.delivery.service.TourService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tours")
public class TourController {

    private final TourService tourService;
    private final TourMapper tourMapper;

    public TourController(TourService tourService, TourMapper tourMapper) {
        this.tourService = tourService;
        this.tourMapper = tourMapper;
    }

    @GetMapping
    public ResponseEntity<List<TourDTO>> getAllTours() {
        try {
            List<TourDTO> tours = tourService.getAllTours().stream()
                    .map(tourMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(tours);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourDTO> getTourById(@PathVariable Long id) {
        try {
            return tourService.getTourById(id)
                    .map(tourMapper::toDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<TourDTO> createTour(@RequestBody TourDTO tourDTO) {
        try {
            Tour tour = tourMapper.toEntity(tourDTO);
            Tour createdTour = tourService.createTour(tour);
            TourDTO createdDTO = tourMapper.toDTO(createdTour);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/{id}/optimize")
    public ResponseEntity<TourDTO> optimizeTour(@PathVariable Long id, @RequestParam Tour.AlgorithmType algorithm) {
        try {
            Tour optimizedTour = tourService.optimizeTour(id, algorithm);
            TourDTO optimizedDTO = tourMapper.toDTO(optimizedTour);
            return ResponseEntity.ok(optimizedDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{id}/optimized-route")
    public ResponseEntity<List<Delivery>> getOptimizedTour(@PathVariable Long id, @RequestParam Tour.AlgorithmType algorithm) {
        try {
            List<Delivery> optimizedRoute = tourService.getOptimizedTour(id, algorithm);
            return ResponseEntity.ok(optimizedRoute);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{id}/total-distance")
    public ResponseEntity<Double> getTotalDistance(@PathVariable Long id, @RequestParam Tour.AlgorithmType algorithm) {
        try {
            Double distance = tourService.getTotalDistance(id, algorithm);
            return ResponseEntity.ok(distance);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<TourDTO>> getToursByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<TourDTO> tours = tourService.getToursByDate(date).stream()
                    .map(tourMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(tours);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<TourDTO>> getToursByVehicle(@PathVariable Long vehicleId) {
        try {
            List<TourDTO> tours = tourService.getToursByVehicle(vehicleId).stream()
                    .map(tourMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(tours);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/algorithm/nearest-neighbor")
    public ResponseEntity<List<TourDTO>> getToursWithNearestNeighbor() {
        try {
            List<TourDTO> tours = tourService.getToursWithNearestNeighbor().stream()
                    .map(tourMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(tours);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/algorithm/clarke-wright")
    public ResponseEntity<List<TourDTO>> getToursWithClarkeWright() {
        try {
            List<TourDTO> tours = tourService.getToursWithClarkeWright().stream()
                    .map(tourMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(tours);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/{tourId}/deliveries/{deliveryId}")
    public ResponseEntity<Void> addDeliveryToTour(@PathVariable Long tourId, @PathVariable Long deliveryId) {
        try {
            tourService.addDeliveryToTour(tourId, deliveryId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}