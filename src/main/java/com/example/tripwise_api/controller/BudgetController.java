package com.example.tripwise_api.controller;

import com.example.tripwise_api.Model.Budget;
import com.example.tripwise_api.Model.Trip;
import com.example.tripwise_api.Repository.BudgetRepository;
import com.example.tripwise_api.Repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/trips")
public class BudgetController {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private TripRepository tripRepository;

    // GET /trips/{tripId}/budget - Retrieve budget for a trip
    @GetMapping("/{tripId}/budget")
    public Optional<Budget> getBudgetByTripId(@PathVariable int tripId) {
        return budgetRepository.findByTrip_TripId(tripId);
    }

    // POST /trips/{tripId}/budget - Create budget for a trip
    @PostMapping("/{tripId}/budget")
    public Budget createBudget(@PathVariable int tripId, @RequestBody Budget budgetDetails) {
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
        Budget budget = budgetRepository.findByTrip_TripId(tripId)
                .orElseGet(() -> {
                    Budget newBudget = new Budget();
                    Trip trip = tripRepository.findById(tripId).orElseThrow();
                    newBudget.setTrip(trip);
                    return newBudget;
                });
        budget.setTotalBudget(budgetDetails.getTotalBudget());
        budget.setCurrency(budgetDetails.getCurrency());
        return budgetRepository.save(budget);
    }

    // DELETE /trips/{tripId}/budget - Delete budget for a trip
    @DeleteMapping("/{tripId}/budget")
    public void deleteBudget(@PathVariable int tripId) {
        budgetRepository.deleteByTrip_TripId(tripId);
    }
}
