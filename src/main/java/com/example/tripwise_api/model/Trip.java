package com.example.tripwise_api.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "trip")
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tripId;
    private Long userId;
    private String tripName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String destination;
    private String description;
    private String transportation;
    private LocalDateTime createdDate;

    // isActive is niet meer een database veld, maar een berekend veld
    @Transient
    private Boolean isActiveOverride; // Alleen voor intern gebruik

    // Constructors
    public Trip() {
        this.createdDate = LocalDateTime.now();
    }

    public Trip(int tripId, Long userId, String tripName, LocalDate startDate, LocalDate endDate,
                String destination, String description, String transportation, LocalDateTime createdDate) {
        this.tripId = tripId;
        this.userId = userId;
        this.tripName = tripName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.destination = destination;
        this.description = description;
        this.transportation = transportation;
        this.createdDate = createdDate;
        // isActive wordt nu automatisch berekend
    }


    // Getters & Setters
    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * isActive wordt automatisch berekend op basis van endDate
     */
    public boolean isActive() {
        if (endDate == null) {
            return true;
        }

        return !endDate.isBefore(LocalDate.now());
    }

    /**
     * Deze setter doet niets - isActive wordt automatisch berekend
     * Maar we houden hem voor backwards compatibility met frontend
     */
    public void setActive(boolean active) {
        // Negeer de waarde - isActive wordt automatisch berekend
    }
    public String getTransportation() {
        return transportation;
    }

    public void setTransportation(String transportation) {
        this.transportation = transportation;
    }

}