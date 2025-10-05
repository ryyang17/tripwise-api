package com.example.tripwise_api.Model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "budget")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int budgetId;
    private int tripId;
    private double totalBudget;
    private String currency;
    private Date createdDate = new Date();

    public Budget() {}

    public Budget(int tripId, double totalBudget, String currency) {
        this.tripId = tripId;
        this.totalBudget = totalBudget;
        this.currency = currency;
    }

    public int getBudgetId() { return budgetId; }
    public void setBudgetId(int budgetId) { this.budgetId = budgetId; }

    public int getTripId() { return tripId; }
    public void setTripId(int tripId) { this.tripId = tripId; }

    public double getTotalBudget() { return totalBudget; }
    public void setTotalBudget(double totalBudget) { this.totalBudget = totalBudget; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
}
