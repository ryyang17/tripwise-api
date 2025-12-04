package com.example.tripwise_api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "expense")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int expenseId;

    private int tripId;
    private String description;
    private BigDecimal amount;

    @Column(name = "original_amount")
    private BigDecimal originalAmount;

    @Column(name = "currency", length = 3)
    private String currency = "EUR";

    @Column(name = "exchange_rate")
    private BigDecimal exchangeRate = BigDecimal.ONE;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private ExpenseCategory category;

    private LocalDate expenseDate;
    private LocalDateTime createdDate = LocalDateTime.now();
    private String receiptUrl;
    private boolean isValidated = false;

    @Column(name = "paid_by")
    private Long paidBy;

    // Add ManyToOne relationship to User for who paid
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paid_by")
    private User paidByUser;

    // Transient helper fields used when creating/updating expenses via API
    @Transient
    private List<Long> splitWithUserIds; // list of user ids participating in split

    @Transient
    private Boolean splitEvenly = Boolean.FALSE; // if true, split amount evenly across splitWithUserIds + paidBy

    // Additional transient fields for frontend compatibility
    @Transient
    private Boolean isSharedExpense = Boolean.FALSE; // indicates if this expense has shares

    @Transient
    private Integer paidById; // participant ID of who paid (for frontend compatibility)

    @Transient
    private List<Integer> splitWith; // list of participant IDs in the split (for frontend compatibility)

    @Transient
    private Map<Integer, Double> customSplits; // custom split amounts per participant ID

    public Expense() {}

    public Expense(int tripId, String description, BigDecimal amount, ExpenseCategory category, LocalDate expenseDate, String receiptUrl) {
        this.tripId = tripId;
        this.description = description;
        this.amount = amount;
        this.originalAmount = amount;
        this.category = category;
        this.expenseDate = expenseDate;
        this.receiptUrl = receiptUrl;
    }

    public int getExpenseId() { return expenseId; }
    public void setExpenseId(int expenseId) { this.expenseId = expenseId; }

    public int getTripId() { return tripId; }
    public void setTripId(int tripId) { this.tripId = tripId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getOriginalAmount() { return originalAmount; }
    public void setOriginalAmount(BigDecimal originalAmount) { this.originalAmount = originalAmount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BigDecimal getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(BigDecimal exchangeRate) { this.exchangeRate = exchangeRate; }

    public ExpenseCategory getCategory() { return category; }
    public void setCategory(ExpenseCategory category) { this.category = category; }

    public LocalDate getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public String getReceiptUrl() { return receiptUrl; }
    public void setReceiptUrl(String receiptUrl) { this.receiptUrl = receiptUrl; }

    public boolean isValidated() { return isValidated; }
    public void setValidated(boolean validated) { isValidated = validated; }

    public Long getPaidBy() { return paidBy; }
    public void setPaidBy(Long paidBy) { this.paidBy = paidBy; }

    public User getPaidByUser() { return paidByUser; }
    public void setPaidByUser(User paidByUser) { this.paidByUser = paidByUser; }

    public List<Long> getSplitWithUserIds() { return splitWithUserIds; }
    public void setSplitWithUserIds(List<Long> splitWithUserIds) { this.splitWithUserIds = splitWithUserIds; }

    public Boolean getSplitEvenly() { return splitEvenly; }
    public void setSplitEvenly(Boolean splitEvenly) { this.splitEvenly = splitEvenly; }

    public Boolean getIsSharedExpense() { return isSharedExpense; }
    public void setIsSharedExpense(Boolean isSharedExpense) { this.isSharedExpense = isSharedExpense; }

    public Integer getPaidById() { return paidById; }
    public void setPaidById(Integer paidById) { this.paidById = paidById; }

    public List<Integer> getSplitWith() { return splitWith; }
    public void setSplitWith(List<Integer> splitWith) { this.splitWith = splitWith; }

    public Map<Integer, Double> getCustomSplits() { return customSplits; }
    public void setCustomSplits(Map<Integer, Double> customSplits) { this.customSplits = customSplits; }

    // Convenience method for frontend compatibility
    public boolean isSharedExpense() {
        return isSharedExpense != null && isSharedExpense;
    }
}
