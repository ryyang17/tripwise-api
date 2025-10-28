package com.example.tripwise_api.dto;

import java.util.List;
import java.util.Map;

public class SharedExpenseRequest {
    private String description;
    private double amount;
    private String currency;
    private int categoryId;
    private String expenseDate;
    private int paidByParticipantId; // Changed from paidById to paidByParticipantId
    private List<Integer> splitWithParticipantIds; // Changed from splitWith to splitWithParticipantIds
    private boolean splitEvenly;

    // New field for custom splits: participantId -> amount
    private Map<Integer, Double> customSplits;

    // Constructors
    public SharedExpenseRequest() {}

    public SharedExpenseRequest(String description, double amount, int categoryId,
                               int paidByParticipantId, List<Integer> splitWithParticipantIds, boolean splitEvenly) {
        this.description = description;
        this.amount = amount;
        this.categoryId = categoryId;
        this.paidByParticipantId = paidByParticipantId;
        this.splitWithParticipantIds = splitWithParticipantIds;
        this.splitEvenly = splitEvenly;
    }

    // Getters and Setters
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

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(String expenseDate) {
        this.expenseDate = expenseDate;
    }

    public int getPaidByParticipantId() {
        return paidByParticipantId;
    }

    public void setPaidByParticipantId(int paidByParticipantId) {
        this.paidByParticipantId = paidByParticipantId;
    }

    public List<Integer> getSplitWithParticipantIds() {
        return splitWithParticipantIds;
    }

    public void setSplitWithParticipantIds(List<Integer> splitWithParticipantIds) {
        this.splitWithParticipantIds = splitWithParticipantIds;
    }

    public boolean isSplitEvenly() {
        return splitEvenly;
    }

    public void setSplitEvenly(boolean splitEvenly) {
        this.splitEvenly = splitEvenly;
    }

    public Map<Integer, Double> getCustomSplits() {
        return customSplits;
    }

    public void setCustomSplits(Map<Integer, Double> customSplits) {
        this.customSplits = customSplits;
    }
}
