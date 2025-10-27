package com.example.tripwise_api.Model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "activity")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long activityId;

    @Column(nullable = false)
    private Long tripId;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "date")
    private LocalDate date; // NEW FIELD - simple date

    @Column(name = "time")
    private LocalTime time; // Updated to use LocalTime instead of String

    private String location;

    @Column(name = "estimated_cost", precision = 10, scale = 2)
    private BigDecimal estimatedCost; // Updated to use BigDecimal instead of Double

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    // Constructors
    public Activity() {
        this.createdDate = LocalDateTime.now();
    }

    public Activity(Long tripId, String name, String location, LocalTime time) {
        this();
        this.tripId = tripId;
        this.name = name;
        this.location = location;
        this.time = time;
    }

    // Business methods
    public void updateTime(LocalTime newTime) {
        this.time = newTime;
    }

    public void updateLocation(String newLocation) {
        this.location = newLocation;
    }

    public boolean hasEstimatedCost() {
        return estimatedCost != null && estimatedCost.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "activityId=" + activityId +
                ", tripId=" + tripId +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", location='" + location + '\'' +
                ", time=" + time +
                ", estimatedCost=" + estimatedCost +
                '}';
    }

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

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public BigDecimal getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
}