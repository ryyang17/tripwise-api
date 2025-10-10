package com.example.tripwise_api.Service;

import com.example.tripwise_api.Model.Budget;
import com.example.tripwise_api.DAL.BudgetDAL;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BudgetService {
    private final BudgetDAL budgetDAL;

    public BudgetService(BudgetDAL budgetDAL) {
        this.budgetDAL = budgetDAL;
    }

    @Transactional(readOnly = true)
    public List<Budget> getAllBudgets() {
        return budgetDAL.getAllBudgets();
    }

    @Transactional(readOnly = true)
    public Optional<Budget> getBudgetById(Integer id) {
        return budgetDAL.getBudgetById(id);
    }

    public Budget createBudget(Budget budget) {
        // Add business validation here if needed
        return budgetDAL.saveBudget(budget);
    }

    public Budget updateBudget(Integer id, Budget budgetDetails) {
        Budget existingBudget = budgetDAL.getBudgetById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found with id: " + id));
        // Update fields as needed
        // ...existing code...
        return budgetDAL.saveBudget(existingBudget);
    }

    public void deleteBudget(Integer id) {
        if (!budgetDAL.existsById(id)) {
            throw new RuntimeException("Budget not found with id: " + id);
        }
        budgetDAL.deleteBudget(id);
    }
}

