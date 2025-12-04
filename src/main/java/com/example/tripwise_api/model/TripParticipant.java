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

    @Column(name = "user_id", nullable = false, insertable = false, updatable = false)
    private Long userId;

    // Add ManyToOne relationship to User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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

    public Long getUserId() { return user != null ? user.getId() : userId; }
    public void setUserId(Long userId) {
        this.userId = userId;
        // Create a User proxy with just the ID to satisfy the not-null constraint
        // This is useful for tests and cases where we only have the userId
        if (userId != null && this.user == null) {
            this.user = new User();
            this.user.setId(userId);
        }
    }

    public User getUser() { return user; }
    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.userId = user.getId();
        }
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDateTime getJoinedDate() { return joinedDate; }
    public void setJoinedDate(LocalDateTime joinedDate) { this.joinedDate = joinedDate; }
}
