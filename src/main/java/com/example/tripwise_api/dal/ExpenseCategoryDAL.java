package com.example.tripwise_api.dal;

import com.example.tripwise_api.model.ExpenseCategory;
import com.example.tripwise_api.repository.ExpenseCategoryRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Component
public class ExpenseCategoryDAL {
    private final ExpenseCategoryRepository expenseCategoryRepository;

    public ExpenseCategoryDAL(ExpenseCategoryRepository expenseCategoryRepository) {
        this.expenseCategoryRepository = expenseCategoryRepository;
    }

    public List<ExpenseCategory> getAllExpenseCategories() {
        return expenseCategoryRepository.findAll();
    }

    public Optional<ExpenseCategory> getExpenseCategoryById(Long id) {
        return expenseCategoryRepository.findById(id);
    }

    public ExpenseCategory saveExpenseCategory(ExpenseCategory expenseCategory) {
        return expenseCategoryRepository.save(expenseCategory);
    }

    public void deleteExpenseCategory(Long id) {
        expenseCategoryRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return expenseCategoryRepository.existsById(id);
    }
}
