package com.example.tripwise_api.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UpdateActivityRequest {
    @NotNull(message = "Activity ID is required")
    private Long activityId;

    @NotNull(message = "Trip ID is required")
    private Long tripId;

    @NotBlank(message = "Activity name is required")
    private String name;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date; // Can be null

    private String time; // HH:MM format or null

    @NotBlank(message = "Location is required")
    private String location;

    private CreateActivityRequest.LocationDetails locationDetails;

    private BigDecimal estimatedCost;

    // Constructors
    public UpdateActivityRequest() {}

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

    public CreateActivityRequest.LocationDetails getLocationDetails() { return locationDetails; }
    public void setLocationDetails(CreateActivityRequest.LocationDetails locationDetails) { this.locationDetails = locationDetails; }

    public BigDecimal getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; }
}

