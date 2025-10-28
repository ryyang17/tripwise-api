package com.example.tripwise_api.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class CreateTripRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Trip name is required")
    private String tripName;

    @NotNull(message = "Start date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotBlank(message = "Destination is required")
    private String destination;

    private DestinationCoords destinationCoords;

    private String description;

    @NotBlank(message = "Transportation is required")
    @Pattern(regexp = "plane|car|bus|train", message = "Transportation must be one of: plane, car, bus, train")
    private String transportation;

    // Nested class for coordinates
    public static class DestinationCoords {
        private Double lat;
        private Double lng;

        public DestinationCoords() {}

        public DestinationCoords(Double lat, Double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        public Double getLat() { return lat; }
        public void setLat(Double lat) { this.lat = lat; }

        public Double getLng() { return lng; }
        public void setLng(Double lng) { this.lng = lng; }
    }

    // Constructors
    public CreateTripRequest() {}

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTripName() { return tripName; }
    public void setTripName(String tripName) { this.tripName = tripName; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public DestinationCoords getDestinationCoords() { return destinationCoords; }
    public void setDestinationCoords(DestinationCoords destinationCoords) { this.destinationCoords = destinationCoords; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTransportation() { return transportation; }
    public void setTransportation(String transportation) { this.transportation = transportation; }
}

