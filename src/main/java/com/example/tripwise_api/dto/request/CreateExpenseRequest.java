package com.example.tripwise_api.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.tripwise_api.model.ExpenseCategory;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class CreateExpenseRequest {
    @NotNull(message = "Trip ID is required")
    private Integer tripId;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private BigDecimal originalAmount;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3-letter ISO code")
    private String currency;

    // Frontend can send embedded category object or just categoryId
    private ExpenseCategory category; // For embedded category from frontend

    private Long categoryId; // Alternative if frontend sends just ID

    @NotNull(message = "Expense date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expenseDate;

    private String receiptUrl;
    private Boolean isValidated = false;

    // Fields for shared expenses
    private Integer paidById; // participant ID
    private List<Integer> splitWith; // list of participant IDs
    private Boolean splitEvenly = false;
    private Boolean isSharedExpense = false;
    private Map<String, BigDecimal> customSplits; // participantId as string -> amount

    // Constructors
    public CreateExpenseRequest() {}

    // Getters and Setters
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

