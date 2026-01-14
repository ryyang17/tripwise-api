package com.example.tripwise_api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class UserResponse {
    private Long id;
    private Long userId;
    private String username;
    private String name;
    private String email;
    private String bio;
    private String location;
    private String favoriteDestination;
    private String travelStyle;
    private Boolean notifications;
    private String currency;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;

    // joinDate is derived from createdDate for display purposes in frontend
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String joinDate; // Optional: formatted date for display

    // Constructors
    public UserResponse() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) {
        this.userId = userId;
        this.id = userId;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getFavoriteDestination() { return favoriteDestination; }
    public void setFavoriteDestination(String favoriteDestination) { this.favoriteDestination = favoriteDestination; }

    public String getTravelStyle() { return travelStyle; }
    public void setTravelStyle(String travelStyle) { this.travelStyle = travelStyle; }

    public Boolean getNotifications() { return notifications; }
    public void setNotifications(Boolean notifications) { this.notifications = notifications; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public String getJoinDate() { return joinDate; }
    public void setJoinDate(String joinDate) { this.joinDate = joinDate; }
}

