package com.example.tripwise_api.Model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int expenseId;

    private int tripId;
    private String description;
    private BigDecimal amount;
    private String category;
    private LocalDate expenseDate;
    private LocalDateTime createdDate = LocalDateTime.now();
    private String receiptUrl;
    private boolean isValidated = false;

    public Expense() {}

    public Expense(int tripId, String description, BigDecimal amount, String category, LocalDate expenseDate, String receiptUrl) {
        this.tripId = tripId;
        this.description = description;
        this.amount = amount;
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

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDate getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public String getReceiptUrl() { return receiptUrl; }
    public void setReceiptUrl(String receiptUrl) { this.receiptUrl = receiptUrl; }

    public boolean isValidated() { return isValidated; }
    public void setValidated(boolean validated) { isValidated = validated; }
}
