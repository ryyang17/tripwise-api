package com.example.tripwise_api.controller;

import com.example.tripwise_api.dto.mapper.ExpenseMapper;
import com.example.tripwise_api.dto.request.CreateExpenseRequest;
import com.example.tripwise_api.dto.request.UpdateExpenseRequest;
import com.example.tripwise_api.dto.request.SharedExpenseRequest;
import com.example.tripwise_api.dto.response.ExpenseResponse;
import com.example.tripwise_api.model.Expense;
import com.example.tripwise_api.model.ExpenseCategory;
import com.example.tripwise_api.service.ExpenseService;
import com.example.tripwise_api.service.ExpenseCategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(originPatterns = "*")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final ExpenseCategoryService expenseCategoryService;

    public ExpenseController(ExpenseService expenseService, ExpenseCategoryService expenseCategoryService) {
        this.expenseService = expenseService;
        this.expenseCategoryService = expenseCategoryService;
    }

    // GET /trips/{tripId}/expenses - All expenses for a trip
    @GetMapping("/trips/{tripId}/expenses")
    public ResponseEntity<List<ExpenseResponse>> getExpensesByTripId(
            @PathVariable int tripId,
            @RequestParam(required = false) String currency) {

        List<Expense> expenses;
        if (currency != null && !currency.isEmpty()) {
            expenses = expenseService.getExpensesByTripIdWithCurrency(tripId, currency);
        } else {
            expenses = expenseService.getExpensesByTripId(tripId);
        }

        // Gebruik mapper om entities naar DTOs te converteren
        return ResponseEntity.ok(ExpenseMapper.toResponseList(expenses));
    }

    // GET /expenses/{id} - Get expense detail
    @GetMapping("/expenses/{id}")
    public ResponseEntity<ExpenseResponse> getExpenseById(@PathVariable int id) {
        Optional<Expense> expense = expenseService.getExpenseById(id);
        return expense.map(e -> ResponseEntity.ok(ExpenseMapper.toResponse(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /trips/{tripId}/expenses - Add new expense to trip
    @PostMapping("/trips/{tripId}/expenses")
    public ResponseEntity<ExpenseResponse> addExpense(
            @PathVariable int tripId,
            @Valid @RequestBody CreateExpenseRequest request,
            @RequestParam(required = false) String targetCurrency) {
        try {
            // STAP 2: Extract categoryId from embedded category OR categoryId field
            Long categoryId = ExpenseMapper.extractCategoryId(request);
            if (categoryId == null) {
                return ResponseEntity.badRequest().build();
            }

            // Haal ExpenseCategory op uit database
            ExpenseCategory category = expenseCategoryService.getCategoryById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + categoryId));

            // Converteer request naar entity
            Expense expense = ExpenseMapper.toEntity(request);
            expense.setCategory(category); // Zet de category

            // Sla op via service
            Expense savedExpense;
            if (targetCurrency != null && !targetCurrency.isEmpty()) {
                savedExpense = expenseService.createExpenseWithCurrency(tripId, expense, targetCurrency);
            } else {
                savedExpense = expenseService.createExpense(tripId, expense);
            }

            // Return response DTO
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ExpenseMapper.toResponse(savedExpense));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT /expenses/{id} - Update expense
    @PutMapping("/expenses/{id}")
    public ResponseEntity<ExpenseResponse> updateExpense(
            @PathVariable int id,
            @Valid @RequestBody UpdateExpenseRequest request) {
        try {
            // Haal bestaande expense op
            Expense existingExpense = expenseService.getExpenseById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Expense not found with id: " + id));

            // STAP 2: Extract categoryId from embedded category OR categoryId field
            Long categoryId = ExpenseMapper.extractCategoryId(request);
            if (categoryId != null) {
                ExpenseCategory category = expenseCategoryService.getCategoryById(categoryId)
                        .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + categoryId));
                existingExpense.setCategory(category);
            }

            // Update entity met request data
            ExpenseMapper.updateEntity(existingExpense, request);

            // Sla op
            Expense updatedExpense = expenseService.updateExpense(id, existingExpense);

            // Return response DTO
            return ResponseEntity.ok(ExpenseMapper.toResponse(updatedExpense));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
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
    public ResponseEntity<ExpenseResponse> addSharedExpense(
            @PathVariable int tripId,
            @Valid @RequestBody SharedExpenseRequest sharedExpenseData) {
        try {
            Expense expense = expenseService.createSharedExpense(tripId, sharedExpenseData);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ExpenseMapper.toResponse(expense));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT /trips/{tripId}/shared-expenses/{expenseId} - Update shared expense
    @PutMapping("/trips/{tripId}/shared-expenses/{expenseId}")
    public ResponseEntity<ExpenseResponse> updateSharedExpense(
            @PathVariable int tripId,
            @PathVariable int expenseId,
            @Valid @RequestBody SharedExpenseRequest sharedExpenseData) {
        try {
            Expense expense = expenseService.updateSharedExpense(tripId, expenseId, sharedExpenseData);
            return ResponseEntity.ok(ExpenseMapper.toResponse(expense));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
