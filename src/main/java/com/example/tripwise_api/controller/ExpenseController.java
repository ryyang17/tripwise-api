package com.example.tripwise_api.controller;

import com.example.tripwise_api.Model.Expense;
import com.example.tripwise_api.Repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    // GET /trips/{tripId}/expenses - Alle expenses voor een trip
    @GetMapping("/trips/{tripId}/expenses")
    public List<Expense> getExpensesByTripId(@PathVariable int tripId) {
        return expenseRepository.findByTripId(tripId);
    }

    // GET /expenses/{id} - Één expense detail ophalen
    @GetMapping("/expenses/{id}")
    public Expense getExpenseById(@PathVariable int id) {
        return expenseRepository.findById(id).orElseThrow();
    }

    // POST /trips/{tripId}/expenses - Nieuwe expense toevoegen aan trip
    @PostMapping("/trips/{tripId}/expenses")
    public Expense addExpense(@PathVariable int tripId, @RequestBody Expense expenseDetails) {
        Expense expense = new Expense();
        expense.setTripId(tripId);
        expense.setDescription(expenseDetails.getDescription());
        expense.setAmount(expenseDetails.getAmount());
        expense.setCategory(expenseDetails.getCategory());
        expense.setExpenseDate(expenseDetails.getExpenseDate());
        expense.setReceiptUrl(expenseDetails.getReceiptUrl());
        expense.setValidated(expenseDetails.isValidated());

        return expenseRepository.save(expense);
    }

    // PUT /expenses/{id} - Update expense
    @PutMapping("/expenses/{id}")
    public Expense updateExpense(@PathVariable int id, @RequestBody Expense expenseDetails) {
        Expense expense = expenseRepository.findById(id).orElseThrow();
        expense.setDescription(expenseDetails.getDescription());
        expense.setAmount(expenseDetails.getAmount());
        expense.setCategory(expenseDetails.getCategory());
        expense.setExpenseDate(expenseDetails.getExpenseDate());
        expense.setReceiptUrl(expenseDetails.getReceiptUrl());
        expense.setValidated(expenseDetails.isValidated());

        return expenseRepository.save(expense);
    }

    // DELETE /expenses/{id} - Verwijder expense
    @DeleteMapping("/expenses/{id}")
    public void deleteExpense(@PathVariable int id) {
        expenseRepository.deleteById(id);
    }
}
