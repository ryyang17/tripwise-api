package com.example.tripwise_api.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trip_participant")
public class TripParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "trip_id", nullable = false)
    private Integer tripId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "role", length = 50)
    private String role;

    @Column(name = "joined_date")
    private LocalDateTime joinedDate = LocalDateTime.now();

    public TripParticipant() {}

    public TripParticipant(Integer tripId, Long userId, String role) {
        this.tripId = tripId;
        this.userId = userId;
        this.role = role;
        this.joinedDate = LocalDateTime.now();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getTripId() { return tripId; }
    public void setTripId(Integer tripId) { this.tripId = tripId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDateTime getJoinedDate() { return joinedDate; }
    public void setJoinedDate(LocalDateTime joinedDate) { this.joinedDate = joinedDate; }
}

