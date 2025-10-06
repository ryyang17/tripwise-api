package com.example.tripwise_api.Model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "budget")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int budgetId;

    @ManyToOne
    @JoinColumn(name = "trip_id", referencedColumnName = "tripId")
    private Trip trip;

    private double totalBudget;
    private String currency;
    private Date createdDate = new Date();

    public Budget() {}

    public Budget(Trip trip, double totalBudget, String currency) {
        this.trip = trip;
        this.totalBudget = totalBudget;
        this.currency = currency;
    }

    public int getBudgetId() { return budgetId; }
    public void setBudgetId(int budgetId) { this.budgetId = budgetId; }

    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }

    public double getTotalBudget() { return totalBudget; }
    public void setTotalBudget(double totalBudget) { this.totalBudget = totalBudget; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
}
