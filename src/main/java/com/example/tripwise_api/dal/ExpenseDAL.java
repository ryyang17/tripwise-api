package com.example.tripwise_api.dal;

import com.example.tripwise_api.model.Expense;
import com.example.tripwise_api.repository.ExpenseRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ExpenseDAL {
    private final ExpenseRepository expenseRepository;

    public ExpenseDAL(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public List<Expense> getExpensesByTripId(int tripId) {
        return expenseRepository.findByTripId(tripId);
    }

    public Optional<Expense> getExpenseById(Integer id) {
        return expenseRepository.findById(id);
    }

    public Expense saveExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    public void deleteExpense(Integer id) {
        expenseRepository.deleteById(id);
    }

    public boolean existsById(Integer id) {
        return expenseRepository.existsById(id);
    }
}
