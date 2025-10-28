package com.example.tripwise_api.dto.request;

import jakarta.validation.constraints.*;

public class CreateUserRequest {
    @NotBlank(message = "Username is required")
    @Size(max = 30, message = "Username max 30 characters")
    private String username;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name max 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 50, message = "Email max 50 characters")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @Size(max = 200, message = "Bio max 200 characters")
    private String bio;

    @Size(max = 100, message = "Location max 100 characters")
    private String location;

    @Size(max = 100, message = "Favorite destination max 100 characters")
    private String favoriteDestination;

    @Size(max = 50, message = "Travel style max 50 characters")
    private String travelStyle;

    private Boolean notifications = true;

    @Size(min = 3, max = 10, message = "Currency must be valid")
    private String currency = "EUR";

    // Constructors
    public CreateUserRequest() {}

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

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
}

