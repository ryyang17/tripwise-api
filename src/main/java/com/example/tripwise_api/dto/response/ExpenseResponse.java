package com.example.tripwise_api.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

public class ExpenseResponse {
    private Long id;
    private String description;
    private double amount;
    private String currency;
    private String category;
    private LocalDateTime expenseDate;
    private String paidByParticipant;
    private Map<String, Double> splits;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDateTime expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getPaidByParticipant() {
        return paidByParticipant;
    }

    public void setPaidByParticipant(String paidByParticipant) {
        this.paidByParticipant = paidByParticipant;
    }

    public Map<String, Double> getSplits() {
        return splits;
    }

    public void setSplits(Map<String, Double> splits) {
        this.splits = splits;
    }
}
