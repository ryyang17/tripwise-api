package com.example.tripwise_api.Model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Long userId;
    private String tripName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String destination;
    private String description;
    private LocalDateTime createdDate = LocalDateTime.now();
    private Boolean isActive = true;

// Constructors
public Trip() {
}

public Trip(int id, Long userId, String tripName, LocalDate startDate, LocalDate endDate,
            String destination, String description, LocalDateTime createdDate, Boolean isActive) {
    this.id = id;
    this.userId = userId;
    this.tripName = tripName;
    this.startDate = startDate;
    this.endDate = endDate;
    this.destination = destination;
    this.description = description;
    this.createdDate = createdDate;
    this.isActive = isActive;
}

// Getters & Setters
public int getId() {
    return id;
}

public void setId(int id) {
    this.id = id;
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

public Boolean getIsActive() {
    return isActive;
}

public void setIsActive(Boolean active) {
    isActive = active;
}
}