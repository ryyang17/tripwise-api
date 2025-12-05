package com.example.tripwise_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public class UpdateParticipantRequest {
    @NotBlank(message = "Role is required")
    private String role;

    private Boolean isOwner = false;

    // Constructors
    public UpdateParticipantRequest(String role, Boolean isOwner) {
        this.role = role;
        this.isOwner = isOwner;
    }

    // Getters and Setters
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Boolean getIsOwner() { return isOwner; }
    public void setIsOwner(Boolean isOwner) { this.isOwner = isOwner; }
}
