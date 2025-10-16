package com.example.tripwise_api.DAL;

import com.example.tripwise_api.Model.Budget;
import com.example.tripwise_api.Repository.BudgetRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BudgetDAL {
    private final BudgetRepository budgetRepository;

    public BudgetDAL(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    // Get all budgets
    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    // Get budget by its ID
    public Optional<Budget> getBudgetById(Integer id) {
        return budgetRepository.findById(id);
    }

    // Save or update a budget
    public Budget saveBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    // Delete budget by its ID
    public void deleteBudget(Integer id) {
        budgetRepository.deleteById(id);
    }

    // Check if a budget exists by its ID
    public boolean existsById(Integer id) {
        return budgetRepository.existsById(id);
    }
}
