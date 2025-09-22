package com.example.tripwise_api.controller;

import com.example.tripwise_api.Model.Budget;
import com.example.tripwise_api.Repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/trips")
@CrossOrigin(origins = "*")
public class BudgetController {

    @Autowired
    private BudgetRepository budgetRepository;

    // GET /trips/{tripId}/budget - Haal budget op voor een trip
    @GetMapping("/{tripId}/budget")
    public Optional<Budget> getBudgetByTripId(@PathVariable int tripId) {
        return budgetRepository.findByTripId(tripId);
    }

    // POST /trips/{tripId}/budget - Maak budget aan voor een trip
    @PostMapping("/{tripId}/budget")
    public Budget createBudget(@PathVariable int tripId, @RequestBody Budget budgetDetails) {
        Budget budget = new Budget();
        budget.setTripId(tripId);
        budget.setTotalBudget(budgetDetails.getTotalBudget());
        budget.setCurrency(budgetDetails.getCurrency());
        return budgetRepository.save(budget);
    }

    // PUT /trips/{tripId}/budget - Update budget van een trip
    @PutMapping("/{tripId}/budget")
    public Budget updateBudget(@PathVariable int tripId, @RequestBody Budget budgetDetails) {
        Budget budget = budgetRepository.findByTripId(tripId).orElseThrow();
        budget.setTotalBudget(budgetDetails.getTotalBudget());
        budget.setCurrency(budgetDetails.getCurrency());
        return budgetRepository.save(budget);
    }

    // DELETE /trips/{tripId}/budget - Verwijder budget van een trip
    @DeleteMapping("/{tripId}/budget")
    public void deleteBudget(@PathVariable int tripId) {
        budgetRepository.deleteByTripId(tripId);
    }
}
