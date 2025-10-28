package com.example.tripwise_api.dto.request;

import jakarta.validation.constraints.NotNull;

public class CreateParticipantRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    private String userName;
    private String userEmail;
    private String role;
    private Boolean isOwner = false;

    // Constructors
    public CreateParticipantRequest() {}

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Boolean getIsOwner() { return isOwner; }
    public void setIsOwner(Boolean isOwner) { this.isOwner = isOwner; }
}

