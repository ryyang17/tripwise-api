package com.example.tripwise_api.dto.request;

import jakarta.validation.constraints.*;

public class UpdateBudgetRequest {
    @NotNull(message = "Total budget is required")
    @Positive(message = "Total budget must be positive")
    private Double totalBudget;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3-letter ISO code")
    private String currency;

    // Constructors
    public UpdateBudgetRequest() {}

    public UpdateBudgetRequest(Double totalBudget, String currency) {
        this.totalBudget = totalBudget;
        this.currency = currency;
    }

    // Getters and Setters
    public Double getTotalBudget() { return totalBudget; }
    public void setTotalBudget(Double totalBudget) { this.totalBudget = totalBudget; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}

