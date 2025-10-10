package com.example.tripwise_api.Service;

import com.example.tripwise_api.Model.ExpenseCategory;
import com.example.tripwise_api.DAL.ExpenseCategoryDAL;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ExpenseCategoryService {
    private final ExpenseCategoryDAL expenseCategoryDAL;

    public ExpenseCategoryService(ExpenseCategoryDAL expenseCategoryDAL) {
        this.expenseCategoryDAL = expenseCategoryDAL;
    }

    @Transactional(readOnly = true)
    public List<ExpenseCategory> getAllExpenseCategories() {
        return expenseCategoryDAL.getAllExpenseCategories();
    }

    @Transactional(readOnly = true)
    public Optional<ExpenseCategory> getExpenseCategoryById(Long id) {
        return expenseCategoryDAL.getExpenseCategoryById(id);
    }

    public ExpenseCategory createExpenseCategory(ExpenseCategory expenseCategory) {
        // Add business validation here if needed
        return expenseCategoryDAL.saveExpenseCategory(expenseCategory);
    }

    public ExpenseCategory updateExpenseCategory(Long id, ExpenseCategory expenseCategoryDetails) {
        ExpenseCategory existingExpenseCategory = expenseCategoryDAL.getExpenseCategoryById(id)
                .orElseThrow(() -> new RuntimeException("ExpenseCategory not found with id: " + id));
        // Update fields as needed
        // ...existing code...
        return expenseCategoryDAL.saveExpenseCategory(existingExpenseCategory);
    }

    public void deleteExpenseCategory(Long id) {
        if (!expenseCategoryDAL.existsById(id)) {
            throw new RuntimeException("ExpenseCategory not found with id: " + id);
        }
        expenseCategoryDAL.deleteExpenseCategory(id);
    }
}

