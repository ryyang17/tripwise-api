package com.example.tripwise_api.controller;

import com.example.tripwise_api.Model.Expense;
import com.example.tripwise_api.Service.ExpenseService;
import com.example.tripwise_api.dto.SharedExpenseRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    // GET /trips/{tripId}/expenses - All expenses for a trip
    @GetMapping("/trips/{tripId}/expenses")
    public ResponseEntity<List<Expense>> getExpensesByTripId(
            @PathVariable int tripId,
            @RequestParam(required = false) String currency) {

        List<Expense> expenses;
        if (currency != null && !currency.isEmpty()) {
            expenses = expenseService.getExpensesByTripIdWithCurrency(tripId, currency);
        } else {
            expenses = expenseService.getExpensesByTripId(tripId);
        }
        return ResponseEntity.ok(expenses);
    }

    // GET /expenses/{id} - Get expense detail
    @GetMapping("/expenses/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable int id) {
        Optional<Expense> expense = expenseService.getExpenseById(id);
        return expense.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /trips/{tripId}/expenses - Add new expense to trip
    @PostMapping("/trips/{tripId}/expenses")
    public ResponseEntity<?> addExpense(
            @PathVariable int tripId,
            @RequestBody Expense expenseDetails,
            @RequestParam(required = false) String targetCurrency) {
        try {
            Expense expense;
            if (targetCurrency != null && !targetCurrency.isEmpty()) {
                expense = expenseService.createExpenseWithCurrency(tripId, expenseDetails, targetCurrency);
            } else {
                expense = expenseService.createExpense(tripId, expenseDetails);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(expense);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT /expenses/{id} - Update expense
    @PutMapping("/expenses/{id}")
    public ResponseEntity<?> updateExpense(@PathVariable int id, @RequestBody Expense expenseDetails) {
        try {
            Expense expense = expenseService.updateExpense(id, expenseDetails);
            return ResponseEntity.ok(expense);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // DELETE /expenses/{id} - Delete expense
    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<String> deleteExpense(@PathVariable int id) {
        try {
            expenseService.deleteExpense(id);
            return ResponseEntity.ok("Expense deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // POST /trips/{tripId}/shared-expenses - Add new shared expense
    @PostMapping("/trips/{tripId}/shared-expenses")
    public ResponseEntity<?> addSharedExpense(
            @PathVariable int tripId,
            @RequestBody SharedExpenseRequest sharedExpenseData) {
        try {
            Expense expense = expenseService.createSharedExpense(tripId, sharedExpenseData);
            return ResponseEntity.status(HttpStatus.CREATED).body(expense);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT /trips/{tripId}/shared-expenses/{expenseId} - Update shared expense
    @PutMapping("/trips/{tripId}/shared-expenses/{expenseId}")
    public ResponseEntity<?> updateSharedExpense(
            @PathVariable int tripId,
            @PathVariable int expenseId,
            @RequestBody SharedExpenseRequest sharedExpenseData) {
        try {
            Expense expense = expenseService.updateSharedExpense(tripId, expenseId, sharedExpenseData);
            return ResponseEntity.ok(expense);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
