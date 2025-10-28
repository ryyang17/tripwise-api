package com.example.tripwise_api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ActivityResponse {
    private Long activityId;
    private Long tripId;
    private String name;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date; // Can be null

    private String time; // HH:MM format or null

    private String location;
    private LocationDetails locationDetails;
    private BigDecimal estimatedCost;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;

    // Nested class for location details
    public static class LocationDetails {
        private String address;
        private Double latitude;
        private Double longitude;
        private String placeId;

        public LocationDetails() {}

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }

        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }

        public String getPlaceId() { return placeId; }
        public void setPlaceId(String placeId) { this.placeId = placeId; }
    }

    // Constructors
    public ActivityResponse() {}

    // Getters and Setters
    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }

    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocationDetails getLocationDetails() { return locationDetails; }
    public void setLocationDetails(LocationDetails locationDetails) { this.locationDetails = locationDetails; }

    public BigDecimal getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
}

