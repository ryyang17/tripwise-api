package com.example.tripwise_api.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class UpdateTripRequest {
    @NotNull(message = "Trip ID is required")
    private Integer tripId;

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

    private CreateTripRequest.DestinationCoords destinationCoords;

    private String description;

    @NotBlank(message = "Transportation is required")
    @Pattern(regexp = "plane|car|bus|train", message = "Transportation must be one of: plane, car, bus, train")
    private String transportation;

    // Constructors
    public UpdateTripRequest() {}

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

    public CreateTripRequest.DestinationCoords getDestinationCoords() { return destinationCoords; }
    public void setDestinationCoords(CreateTripRequest.DestinationCoords destinationCoords) { this.destinationCoords = destinationCoords; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTransportation() { return transportation; }
    public void setTransportation(String transportation) { this.transportation = transportation; }
}

