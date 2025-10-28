package com.example.tripwise_api.dto.mapper;

import com.example.tripwise_api.dto.request.CreateExpenseRequest;
import com.example.tripwise_api.dto.request.UpdateExpenseRequest;
import com.example.tripwise_api.dto.response.ExpenseResponse;
import com.example.tripwise_api.model.Expense;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mapper voor het converteren tussen Expense entities en DTOs
 */
public class ExpenseMapper {

    /**
     * Convert Expense entity naar ExpenseResponse DTO
     */
    public static ExpenseResponse toResponse(Expense expense) {
        if (expense == null) return null;

        ExpenseResponse response = new ExpenseResponse();
        response.setExpenseId(expense.getExpenseId());
        response.setTripId(expense.getTripId());
        response.setDescription(expense.getDescription());
        response.setAmount(expense.getAmount());
        response.setOriginalAmount(expense.getOriginalAmount());
        response.setCurrency(expense.getCurrency());
        response.setCategory(expense.getCategory()); // Full category object
        response.setExpenseDate(expense.getExpenseDate());
        response.setCreatedDate(expense.getCreatedDate());
        response.setReceiptUrl(expense.getReceiptUrl());
        response.setIsValidated(expense.isValidated()); // Note: isValidated() method, not getIsValidated()

        // Shared expense fields - convert types if needed
        response.setPaidById(expense.getPaidById());
        response.setSplitWith(expense.getSplitWith());
        response.setSplitEvenly(expense.getSplitEvenly());
        response.setIsSharedExpense(expense.getIsSharedExpense());

        // Convert Map<Integer, Double> to Map<String, BigDecimal> for frontend
        if (expense.getCustomSplits() != null) {
            Map<String, BigDecimal> convertedSplits = new HashMap<>();
            expense.getCustomSplits().forEach((key, value) ->
                    convertedSplits.put(String.valueOf(key), BigDecimal.valueOf(value))
            );
            response.setCustomSplits(convertedSplits);
        }

        return response;
    }

    /**
     * Convert lijst van Expense entities naar lijst van ExpenseResponse DTOs
     */
    public static List<ExpenseResponse> toResponseList(List<Expense> expenses) {
        if (expenses == null) return null;
        return expenses.stream()
                .map(ExpenseMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convert CreateExpenseRequest naar Expense entity
     * STAP 2: Haalt categoryId uit embedded category object OF uit categoryId field
     */
    public static Expense toEntity(CreateExpenseRequest request) {
        if (request == null) return null;

        Expense expense = new Expense();
        expense.setTripId(request.getTripId());
        expense.setDescription(request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setOriginalAmount(request.getOriginalAmount() != null ?
                                  request.getOriginalAmount() : request.getAmount());
        expense.setCurrency(request.getCurrency());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setReceiptUrl(request.getReceiptUrl());
        expense.setValidated(request.getIsValidated() != null ? request.getIsValidated() : false);

        // Shared expense fields - convert types
        expense.setPaidById(request.getPaidById());
        expense.setSplitWith(request.getSplitWith());
        expense.setSplitEvenly(request.getSplitEvenly() != null ? request.getSplitEvenly() : false);
        expense.setIsSharedExpense(request.getIsSharedExpense() != null ? request.getIsSharedExpense() : false);

        // Convert Map<String, BigDecimal> from frontend to Map<Integer, Double> for entity
        if (request.getCustomSplits() != null) {
            Map<Integer, Double> convertedSplits = new HashMap<>();
            request.getCustomSplits().forEach((key, value) ->
                    convertedSplits.put(Integer.parseInt(key), value.doubleValue())
            );
            expense.setCustomSplits(convertedSplits);
        }

        // STAP 2: Category wordt NIET hier gezet - controller moet dit doen via service
        // omdat we ExpenseCategory entity moeten ophalen uit database

        return expense;
    }

    /**
     * STAP 2: Haal categoryId op uit request
     * Frontend kan OFWEL embedded category object sturen OFWEL alleen categoryId
     */
    public static Long extractCategoryId(CreateExpenseRequest request) {
        if (request == null) return null;

        // Eerst proberen: embedded category object (zoals frontend nu stuurt)
        if (request.getCategory() != null && request.getCategory().getCategoryId() != null) {
            return request.getCategory().getCategoryId();
        }

        // Anders: directe categoryId field
        return request.getCategoryId();
    }

    /**
     * STAP 2: Haal categoryId op uit update request
     */
    public static Long extractCategoryId(UpdateExpenseRequest request) {
        if (request == null) return null;

        // Eerst proberen: embedded category object
        if (request.getCategory() != null && request.getCategory().getCategoryId() != null) {
            return request.getCategory().getCategoryId();
        }

        // Anders: directe categoryId field
        return request.getCategoryId();
    }

    /**
     * Update bestaande Expense entity met UpdateExpenseRequest data
     */
    public static void updateEntity(Expense expense, UpdateExpenseRequest request) {
        if (expense == null || request == null) return;

        if (request.getTripId() != null) {
            expense.setTripId(request.getTripId());
        }
        if (request.getDescription() != null) {
            expense.setDescription(request.getDescription());
        }
        if (request.getAmount() != null) {
            expense.setAmount(request.getAmount());
        }
        if (request.getOriginalAmount() != null) {
            expense.setOriginalAmount(request.getOriginalAmount());
        }
        if (request.getCurrency() != null) {
            expense.setCurrency(request.getCurrency());
        }
        if (request.getExpenseDate() != null) {
            expense.setExpenseDate(request.getExpenseDate());
        }
        if (request.getReceiptUrl() != null) {
            expense.setReceiptUrl(request.getReceiptUrl());
        }
        if (request.getIsValidated() != null) {
            expense.setValidated(request.getIsValidated());
        }

        // Shared expense fields
        if (request.getPaidById() != null) {
            expense.setPaidById(request.getPaidById());
        }
        if (request.getSplitWith() != null) {
            expense.setSplitWith(request.getSplitWith());
        }
        if (request.getSplitEvenly() != null) {
            expense.setSplitEvenly(request.getSplitEvenly());
        }
        if (request.getIsSharedExpense() != null) {
            expense.setIsSharedExpense(request.getIsSharedExpense());
        }
        if (request.getCustomSplits() != null) {
            // Convert Map<String, BigDecimal> to Map<Integer, Double>
            Map<Integer, Double> convertedSplits = new HashMap<>();
            request.getCustomSplits().forEach((key, value) ->
                    convertedSplits.put(Integer.parseInt(key), value.doubleValue())
            );
            expense.setCustomSplits(convertedSplits);
        }

        // Category wordt apart gezet via service
    }
}
