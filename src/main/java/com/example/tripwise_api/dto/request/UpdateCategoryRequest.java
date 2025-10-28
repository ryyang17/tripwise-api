package com.example.tripwise_api.dto.request;

import jakarta.validation.constraints.*;

public class UpdateCategoryRequest {
    private String categoryName;
    private String categoryDescription;
    private Boolean isActive;

    // Constructors
    public UpdateCategoryRequest() {}

    // Getters and Setters
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getCategoryDescription() { return categoryDescription; }
    public void setCategoryDescription(String categoryDescription) { this.categoryDescription = categoryDescription; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}

