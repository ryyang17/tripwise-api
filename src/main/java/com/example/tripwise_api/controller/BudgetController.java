package com.example.tripwise_api.controller;

import com.example.tripwise_api.model.Budget;
import com.example.tripwise_api.service.BudgetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips")
@CrossOrigin(origins = "*")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

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
    public ResponseEntity<Budget> createBudget(@PathVariable int tripId, @RequestBody Budget budgetDetails) {
        try {
            Budget budget = budgetService.createBudget(tripId, budgetDetails);
            return ResponseEntity.status(HttpStatus.CREATED).body(budget);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT /trips/{tripId}/budget - Update or create budget for a trip
    @PutMapping("/{tripId}/budget")
    public ResponseEntity<Budget> updateOrCreateBudget(@PathVariable int tripId, @RequestBody Budget budgetDetails) {
        try {
            Budget budget = budgetService.updateOrCreateBudget(tripId, budgetDetails);
            return ResponseEntity.ok(budget);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
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
