package com.example.tripwise_api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public class BudgetResponse {
    private Integer budgetId;
    private Integer tripId;
    private TripSummary trip; // Optional subset of trip info
    private Double totalBudget;
    private String currency;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;

    private List<CategorySpent> categories; // Optional: spending per category

    // Nested class for trip summary
    public static class TripSummary {
        private Integer tripId;
        private String tripName;
        private String destination;

        public TripSummary() {}

        public Integer getTripId() { return tripId; }
        public void setTripId(Integer tripId) { this.tripId = tripId; }

        public String getTripName() { return tripName; }
        public void setTripName(String tripName) { this.tripName = tripName; }

        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }
    }

    // Nested class for category spending
    public static class CategorySpent {
        private Long categoryId;
        private String categoryName;
        private Double spent;

        public CategorySpent() {}

        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

        public Double getSpent() { return spent; }
        public void setSpent(Double spent) { this.spent = spent; }
    }

    // Constructors
    public BudgetResponse() {}

    // Getters and Setters
    public Integer getBudgetId() { return budgetId; }
    public void setBudgetId(Integer budgetId) { this.budgetId = budgetId; }

    public Integer getTripId() { return tripId; }
    public void setTripId(Integer tripId) { this.tripId = tripId; }

    public TripSummary getTrip() { return trip; }
    public void setTrip(TripSummary trip) { this.trip = trip; }

    public Double getTotalBudget() { return totalBudget; }
    public void setTotalBudget(Double totalBudget) { this.totalBudget = totalBudget; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public List<CategorySpent> getCategories() { return categories; }
    public void setCategories(List<CategorySpent> categories) { this.categories = categories; }
}

