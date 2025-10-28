package com.example.tripwise_api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class TripParticipantResponse {
    private Integer participantId; // Canonical naam voor frontend
    private Integer id; // Alternative key for backwards compatibility
    private Integer tripId;
    private Long userId;
    private String userName;
    private String userEmail;
    private String role;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime joinedDate;

    private Boolean isOwner;

    // Constructors
    public TripParticipantResponse() {}

    // Getters and Setters
    public Integer getParticipantId() { return participantId; }
    public void setParticipantId(Integer participantId) {
        this.participantId = participantId;
        this.id = participantId; // Keep in sync
    }

    public Integer getId() { return id; }
    public void setId(Integer id) {
        this.id = id;
        if (this.participantId == null) {
            this.participantId = id;
        }
    }

    public Integer getTripId() { return tripId; }
    public void setTripId(Integer tripId) { this.tripId = tripId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDateTime getJoinedDate() { return joinedDate; }
    public void setJoinedDate(LocalDateTime joinedDate) { this.joinedDate = joinedDate; }

    public Boolean getIsOwner() { return isOwner; }
    public void setIsOwner(Boolean isOwner) { this.isOwner = isOwner; }
}

