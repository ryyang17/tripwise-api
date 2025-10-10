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

    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    public Optional<Budget> getBudgetById(Integer id) {
        return budgetRepository.findById(id);
    }

    public Budget saveBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    public void deleteBudget(Integer id) {
        budgetRepository.deleteById(id);
    }

    public boolean existsById(Integer id) {
        return budgetRepository.existsById(id);
    }
}

