package com.example.tripwise_api.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class SharedExpenseRequest {
    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3-letter ISO code")
    private String currency;

    @NotNull(message = "Category ID is required")
    private Integer categoryId;

    @NotNull(message = "Expense date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expenseDate;

    @NotNull(message = "Paid by participant ID is required")
    private Integer paidByParticipantId;

    @NotEmpty(message = "Split with participant IDs is required")
    private List<Integer> splitWithParticipantIds;

    private Boolean splitEvenly = false;

    // Custom splits: participantId as string -> amount
    private Map<String, BigDecimal> customSplits;

    // Constructors
    public SharedExpenseRequest() {}

    // Getters and Setters
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public LocalDate getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }

    public Integer getPaidByParticipantId() { return paidByParticipantId; }
    public void setPaidByParticipantId(Integer paidByParticipantId) { this.paidByParticipantId = paidByParticipantId; }

    public List<Integer> getSplitWithParticipantIds() { return splitWithParticipantIds; }
    public void setSplitWithParticipantIds(List<Integer> splitWithParticipantIds) { this.splitWithParticipantIds = splitWithParticipantIds; }

    public Boolean getSplitEvenly() { return splitEvenly; }
    public void setSplitEvenly(Boolean splitEvenly) { this.splitEvenly = splitEvenly; }

    public Map<String, BigDecimal> getCustomSplits() { return customSplits; }
    public void setCustomSplits(Map<String, BigDecimal> customSplits) { this.customSplits = customSplits; }
}
