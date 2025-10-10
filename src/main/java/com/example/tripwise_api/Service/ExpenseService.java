package com.example.tripwise_api.Service;

import com.example.tripwise_api.Model.Expense;
import com.example.tripwise_api.DAL.ExpenseDAL;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ExpenseService {
    private final ExpenseDAL expenseDAL;

    public ExpenseService(ExpenseDAL expenseDAL) {
        this.expenseDAL = expenseDAL;
    }

    @Transactional(readOnly = true)
    public List<Expense> getAllExpenses() {
        return expenseDAL.getAllExpenses();
    }

    @Transactional(readOnly = true)
    public Optional<Expense> getExpenseById(Integer id) {
        return expenseDAL.getExpenseById(id);
    }

    public Expense createExpense(Expense expense) {
        // Add business validation here if needed
        return expenseDAL.saveExpense(expense);
    }

    public Expense updateExpense(Integer id, Expense expenseDetails) {
        Expense existingExpense = expenseDAL.getExpenseById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));
        return expenseDAL.saveExpense(existingExpense);
    }

    public void deleteExpense(Integer id) {
        if (!expenseDAL.existsById(id)) {
            throw new RuntimeException("Expense not found with id: " + id);
        }
        expenseDAL.deleteExpense(id);
    }
}

