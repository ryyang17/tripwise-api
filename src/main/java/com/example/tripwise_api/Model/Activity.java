package com.example.tripwise_api.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "activitiy")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long activityId;

    @Column(nullable = false)
    private Long tripId;

    @Column(nullable = false)
    private String name;

    private String description;

    private String time;

    private String location;

    private Double estimatedCost;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    // Constructors
    public Activity() {
        this.createdDate = LocalDateTime.now();
    }

    public Activity(Long tripId, String name, String location, String time) {
        this();
        this.tripId = tripId;
        this.name = name;
        this.location = location;
        this.time = time;
    }

    // Business methods
    public void updateTime(String newTime) {
        this.time = newTime;
    }

    public void updateLocation(String newLocation) {
        this.location = newLocation;
    }

    public boolean hasEstimatedCost() {
        return estimatedCost != null && estimatedCost > 0;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "activityId=" + activityId +
                ", tripId=" + tripId +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", time='" + time + '\'' +
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

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Double getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(Double estimatedCost) { this.estimatedCost = estimatedCost; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
}