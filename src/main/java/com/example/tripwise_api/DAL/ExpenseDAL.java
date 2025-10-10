package com.example.tripwise_api.DAL;

import com.example.tripwise_api.Model.Expense;
import com.example.tripwise_api.Repository.ExpenseRepository;
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

