package com.example.tripwise_api.dto.request;

import jakarta.validation.constraints.*;

public class CreateCategoryRequest {
    @NotBlank(message = "Category name is required")
    @Size(max = 50, message = "Category name max 50 characters")
    private String categoryName;

    @Size(max = 200, message = "Category description max 200 characters")
    private String categoryDescription;

    private Boolean isActive = true;

    // Constructors
    public CreateCategoryRequest() {}

    public CreateCategoryRequest(String categoryName, String categoryDescription) {
        this.categoryName = categoryName;
        this.categoryDescription = categoryDescription;
    }

    // Getters and Setters
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getCategoryDescription() { return categoryDescription; }
    public void setCategoryDescription(String categoryDescription) { this.categoryDescription = categoryDescription; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}

