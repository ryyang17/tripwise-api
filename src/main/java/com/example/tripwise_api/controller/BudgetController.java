package com.example.tripwise_api.controller;

import com.example.tripwise_api.Model.Budget;
import com.example.tripwise_api.Service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    // GET /trips/{tripId}/budget - Retrieve budget for a trip
    @GetMapping("/{tripId}/budget")
    public ResponseEntity<Budget> getBudgetByTripId(@PathVariable int tripId) {
        Budget budget = budgetService.getBudgetByTripId(tripId);
        if (budget == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(budget);
    }

    // POST /trips/{tripId}/budget - Create budget for a trip
    @PostMapping("/{tripId}/budget")
    public ResponseEntity<?> createBudget(@PathVariable int tripId, @RequestBody Budget budgetDetails) {
        try {
            Budget budget = budgetService.createBudget(tripId, budgetDetails);
            return ResponseEntity.status(HttpStatus.CREATED).body(budget);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT /trips/{tripId}/budget - Update or create budget for a trip
    @PutMapping("/{tripId}/budget")
    public ResponseEntity<?> updateOrCreateBudget(@PathVariable int tripId, @RequestBody Budget budgetDetails) {
        try {
            Budget budget = budgetService.updateOrCreateBudget(tripId, budgetDetails);
            return ResponseEntity.ok(budget);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // DELETE /trips/{tripId}/budget - Delete budget for a trip
    @DeleteMapping("/{tripId}/budget")
    public ResponseEntity<String> deleteBudget(@PathVariable int tripId) {
        try {
            budgetService.deleteBudget(tripId);
            return ResponseEntity.ok("Budget deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
