package com.example.tripwise_api.Service;

import com.example.tripwise_api.Model.Expense;
import com.example.tripwise_api.DAL.ExpenseDAL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ExpenseService {

    @Autowired
    private ExpenseDAL expenseDAL;

    @Autowired
    private CurrencyService currencyService;

    @Transactional(readOnly = true)
    public List<Expense> getAllExpenses() {
        return expenseDAL.getAllExpenses();
    }

    @Transactional(readOnly = true)
    public List<Expense> getExpensesByTripId(int tripId) {
        return expenseDAL.getExpensesByTripId(tripId);
    }


     // Get expenses by trip ID with currency conversion to user's preferred currency

    @Transactional(readOnly = true)
    public List<Expense> getExpensesByTripIdWithCurrency(int tripId, String targetCurrency) {
        List<Expense> expenses = expenseDAL.getExpensesByTripId(tripId);

        if (targetCurrency != null && !targetCurrency.isEmpty()) {
            for (Expense expense : expenses) {
                convertExpenseCurrency(expense, targetCurrency);
            }
        }

        return expenses;
    }

    @Transactional(readOnly = true)
    public Optional<Expense> getExpenseById(int id) {
        return expenseDAL.getExpenseById(id);
    }

    public Expense createExpense(int tripId, Expense expenseDetails) {
        Expense expense = new Expense();
        expense.setTripId(tripId);
        expense.setDescription(expenseDetails.getDescription());

        // Handle currency conversion
        String expenseCurrency = expenseDetails.getCurrency() != null ?
            expenseDetails.getCurrency() : "EUR";
        BigDecimal originalAmount = expenseDetails.getOriginalAmount() != null ?
            expenseDetails.getOriginalAmount() : expenseDetails.getAmount();

        expense.setOriginalAmount(originalAmount);
        expense.setCurrency(expenseCurrency);

        // If amount is different from original amount, it means conversion was already done
        // Otherwise, use the original amount as the amount
        if (expenseDetails.getAmount().equals(originalAmount)) {
            expense.setAmount(originalAmount);
            expense.setExchangeRate(BigDecimal.ONE);
        } else {
            expense.setAmount(expenseDetails.getAmount());
            // Calculate exchange rate using RoundingMode enum
            BigDecimal rate = expenseDetails.getAmount().divide(originalAmount, 6, RoundingMode.HALF_UP);
            expense.setExchangeRate(rate);
        }

        expense.setCategory(expenseDetails.getCategory());
        expense.setExpenseDate(expenseDetails.getExpenseDate());
        expense.setReceiptUrl(expenseDetails.getReceiptUrl());
        expense.setValidated(expenseDetails.isValidated());

        return expenseDAL.saveExpense(expense);
    }


     //Create expense with automatic currency conversion

    public Expense createExpenseWithCurrency(int tripId, Expense expenseDetails, String targetCurrency) {
        // Set original values
        expenseDetails.setOriginalAmount(expenseDetails.getAmount());

        String expenseCurrency = expenseDetails.getCurrency() != null ?
            expenseDetails.getCurrency() : "EUR";
        expenseDetails.setCurrency(expenseCurrency);

        // Convert to target currency if different
        if (targetCurrency != null && !expenseCurrency.equals(targetCurrency)) {
            BigDecimal exchangeRate = currencyService.getExchangeRate(expenseCurrency, targetCurrency);
            BigDecimal convertedAmount = expenseDetails.getAmount().multiply(exchangeRate);

            expenseDetails.setAmount(convertedAmount);
            expenseDetails.setExchangeRate(exchangeRate);
        } else {
            expenseDetails.setExchangeRate(BigDecimal.ONE);
        }

        return createExpense(tripId, expenseDetails);
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

        // Update currency fields if provided
        if (expenseDetails.getOriginalAmount() != null) {
            expense.setOriginalAmount(expenseDetails.getOriginalAmount());
        }
        if (expenseDetails.getCurrency() != null) {
            expense.setCurrency(expenseDetails.getCurrency());
        }
        if (expenseDetails.getExchangeRate() != null) {
            expense.setExchangeRate(expenseDetails.getExchangeRate());
        }

        return expenseDAL.saveExpense(expense);
    }


     // Convert expense amount to target currency

    private void convertExpenseCurrency(Expense expense, String targetCurrency) {
        if (!expense.getCurrency().equals(targetCurrency)) {
            BigDecimal exchangeRate = currencyService.getExchangeRate(expense.getCurrency(), targetCurrency);
            BigDecimal convertedAmount = expense.getOriginalAmount().multiply(exchangeRate);
            expense.setAmount(convertedAmount);
        }
    }

    public void deleteExpense(int id) {
        if (!expenseDAL.existsById(id)) {
            throw new IllegalArgumentException("Expense not found with id: " + id);
        }
        expenseDAL.deleteExpense(id);
    }
}
