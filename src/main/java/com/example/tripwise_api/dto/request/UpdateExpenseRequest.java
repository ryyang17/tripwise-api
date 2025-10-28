package com.example.tripwise_api.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.tripwise_api.model.ExpenseCategory;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class UpdateExpenseRequest {
    @NotNull(message = "Expense ID is required")
    private Integer expenseId;

    private Integer tripId;
    private String description;
    private BigDecimal amount;
    private BigDecimal originalAmount;
    private String currency;

    // Frontend can send embedded category object or just categoryId
    private ExpenseCategory category;
    private Long categoryId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expenseDate;

    private String receiptUrl;
    private Boolean isValidated;

    // Fields for shared expenses
    private Integer paidById;
    private List<Integer> splitWith;
    private Boolean splitEvenly;
    private Boolean isSharedExpense;
    private Map<String, BigDecimal> customSplits;

    // Constructors
    public UpdateExpenseRequest() {}

    // Getters and Setters
    public Integer getExpenseId() { return expenseId; }
    public void setExpenseId(Integer expenseId) { this.expenseId = expenseId; }

    public Integer getTripId() { return tripId; }
    public void setTripId(Integer tripId) { this.tripId = tripId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getOriginalAmount() { return originalAmount; }
    public void setOriginalAmount(BigDecimal originalAmount) { this.originalAmount = originalAmount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public ExpenseCategory getCategory() { return category; }
    public void setCategory(ExpenseCategory category) { this.category = category; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public LocalDate getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }

    public String getReceiptUrl() { return receiptUrl; }
    public void setReceiptUrl(String receiptUrl) { this.receiptUrl = receiptUrl; }

    public Boolean getIsValidated() { return isValidated; }
    public void setIsValidated(Boolean isValidated) { this.isValidated = isValidated; }

    public Integer getPaidById() { return paidById; }
    public void setPaidById(Integer paidById) { this.paidById = paidById; }

    public List<Integer> getSplitWith() { return splitWith; }
    public void setSplitWith(List<Integer> splitWith) { this.splitWith = splitWith; }

    public Boolean getSplitEvenly() { return splitEvenly; }
    public void setSplitEvenly(Boolean splitEvenly) { this.splitEvenly = splitEvenly; }

    public Boolean getIsSharedExpense() { return isSharedExpense; }
    public void setIsSharedExpense(Boolean isSharedExpense) { this.isSharedExpense = isSharedExpense; }

    public Map<String, BigDecimal> getCustomSplits() { return customSplits; }
    public void setCustomSplits(Map<String, BigDecimal> customSplits) { this.customSplits = customSplits; }
}

