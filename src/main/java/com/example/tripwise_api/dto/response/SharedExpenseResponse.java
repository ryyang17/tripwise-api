package com.example.tripwise_api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class SharedExpenseResponse {
    private Integer expenseId;
    private Integer tripId;
    private String description;
    private BigDecimal amount;
    private String currency;
    private Long categoryId;
    private String categoryName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expenseDate;

    private Integer paidByParticipantId;
    private String paidByParticipantName; // Optional: naam van betaler

    private Map<Integer, BigDecimal> splits; // participantId -> amount owed
    private Boolean splitEvenly;

    // Constructors
    public SharedExpenseResponse() {}

    // Getters and Setters
    public Integer getExpenseId() { return expenseId; }
    public void setExpenseId(Integer expenseId) { this.expenseId = expenseId; }

    public Integer getTripId() { return tripId; }
    public void setTripId(Integer tripId) { this.tripId = tripId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public LocalDate getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }

    public Integer getPaidByParticipantId() { return paidByParticipantId; }
    public void setPaidByParticipantId(Integer paidByParticipantId) { this.paidByParticipantId = paidByParticipantId; }

    public String getPaidByParticipantName() { return paidByParticipantName; }
    public void setPaidByParticipantName(String paidByParticipantName) { this.paidByParticipantName = paidByParticipantName; }

    public Map<Integer, BigDecimal> getSplits() { return splits; }
    public void setSplits(Map<Integer, BigDecimal> splits) { this.splits = splits; }

    public Boolean getSplitEvenly() { return splitEvenly; }
    public void setSplitEvenly(Boolean splitEvenly) { this.splitEvenly = splitEvenly; }
}

