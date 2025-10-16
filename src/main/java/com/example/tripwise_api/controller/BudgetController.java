package com.example.tripwise_api.controller;

import com.example.tripwise_api.Model.Budget;
import com.example.tripwise_api.Model.Trip;
import com.example.tripwise_api.Repository.BudgetRepository;
import com.example.tripwise_api.Repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class BudgetController {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private TripRepository tripRepository;

    // GET /trips/{tripId}/budget - Retrieve budget for a trip
    @GetMapping("/{tripId}/budget")
    public Budget getBudgetByTripId(@PathVariable int tripId) {
        List<Budget> budgets = budgetRepository.findByTrip_TripId(tripId);
        if (budgets.isEmpty()) return null;
        // Return the most recent budget (last in list)
        return budgets.get(budgets.size() - 1);
    }

    // POST /trips/{tripId}/budget - Create budget for a trip
    @PostMapping("/{tripId}/budget")
    public Budget createBudget(@PathVariable int tripId, @RequestBody Budget budgetDetails) {
        // Delete existing budgets for this trip first
        List<Budget> existingBudgets = budgetRepository.findByTrip_TripId(tripId);
        existingBudgets.forEach(budget -> budgetRepository.deleteById(budget.getBudgetId()));

        Budget budget = new Budget();
        Trip trip = tripRepository.findById(tripId).orElseThrow();
        budget.setTrip(trip);
        budget.setTotalBudget(budgetDetails.getTotalBudget());
        budget.setCurrency(budgetDetails.getCurrency());
        return budgetRepository.save(budget);
    }

    // PUT /trips/{tripId}/budget - Update or create budget for a trip
    @PutMapping("/{tripId}/budget")
    public Budget updateOrCreateBudget(@PathVariable int tripId, @RequestBody Budget budgetDetails) {
        List<Budget> budgets = budgetRepository.findByTrip_TripId(tripId);
        Budget budget;

        if (!budgets.isEmpty()) {
            // Update the most recent budget
            budget = budgets.get(budgets.size() - 1);
            // Delete any other old budgets for this trip using standard JPA
            for (int i = 0; i < budgets.size() - 1; i++) {
                budgetRepository.deleteById(budgets.get(i).getBudgetId());
            }
        } else {
            // Create new budget
            budget = new Budget();
            Trip trip = tripRepository.findById(tripId).orElseThrow();
            budget.setTrip(trip);
        }

        budget.setTotalBudget(budgetDetails.getTotalBudget());
        budget.setCurrency(budgetDetails.getCurrency());
        return budgetRepository.save(budget);
    }

    // DELETE /trips/{tripId}/budget - Delete budget for a trip (using standard JPA)
    @DeleteMapping("/{tripId}/budget")
    public void deleteBudget(@PathVariable int tripId) {
        List<Budget> budgets = budgetRepository.findByTrip_TripId(tripId);
        budgets.forEach(budget -> budgetRepository.deleteById(budget.getBudgetId()));
    }
}
