package com.example.tripwise_api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TripResponse {
    private Integer tripId;
    private Long userId;
    private String tripName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String destination;
    private DestinationCoords destinationCoords;
    private String description;
    private String transportation;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;

    private Boolean isActive; // Computed by backend based on endDate

    // Owner information fields
    private String ownerName;
    private String ownerEmail;

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
    public TripResponse() {}

    // Getters and Setters
    public Integer getTripId() { return tripId; }
    public void setTripId(Integer tripId) { this.tripId = tripId; }

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

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }
}
