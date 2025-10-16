package com.example.tripwise_api.Service;

import com.example.tripwise_api.Model.Expense;
import com.example.tripwise_api.DAL.ExpenseDAL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ExpenseService {

    @Autowired
    private ExpenseDAL expenseDAL;

    @Transactional(readOnly = true)
    public List<Expense> getAllExpenses() {
        return expenseDAL.getAllExpenses();
    }

    @Transactional(readOnly = true)
    public List<Expense> getExpensesByTripId(int tripId) {
        return expenseDAL.getExpensesByTripId(tripId);
    }

    @Transactional(readOnly = true)
    public Optional<Expense> getExpenseById(int id) {
        return expenseDAL.getExpenseById(id);
    }

    public Expense createExpense(int tripId, Expense expenseDetails) {
        Expense expense = new Expense();
        expense.setTripId(tripId);
        expense.setDescription(expenseDetails.getDescription());
        expense.setAmount(expenseDetails.getAmount());
        expense.setCategory(expenseDetails.getCategory());
        expense.setExpenseDate(expenseDetails.getExpenseDate());
        expense.setReceiptUrl(expenseDetails.getReceiptUrl());
        expense.setValidated(expenseDetails.isValidated());

        return expenseDAL.saveExpense(expense);
    }

    public Expense updateExpense(int id, Expense expenseDetails) {
        Expense expense = expenseDAL.getExpenseById(id)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found with id: " + id));

        expense.setDescription(expenseDetails.getDescription());
        expense.setAmount(expenseDetails.getAmount());
        expense.setCategory(expenseDetails.getCategory());
        expense.setExpenseDate(expenseDetails.getExpenseDate());
        expense.setReceiptUrl(expenseDetails.getReceiptUrl());
        expense.setValidated(expenseDetails.isValidated());

        return expenseDAL.saveExpense(expense);
    }

    public void deleteExpense(int id) {
        if (!expenseDAL.existsById(id)) {
            throw new IllegalArgumentException("Expense not found with id: " + id);
        }
        expenseDAL.deleteExpense(id);
    }
}
