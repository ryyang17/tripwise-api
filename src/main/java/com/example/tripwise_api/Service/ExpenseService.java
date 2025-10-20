package com.example.tripwise_api.Service;

import com.example.tripwise_api.Model.Expense;
import com.example.tripwise_api.Model.ExpenseShare;
import com.example.tripwise_api.Model.ExpenseCategory;
import com.example.tripwise_api.Model.TripParticipant;
import com.example.tripwise_api.DAL.ExpenseDAL;
import com.example.tripwise_api.DAL.ExpenseShareDAL;
import com.example.tripwise_api.Service.ExpenseCategoryService;
import com.example.tripwise_api.Service.TripParticipantService;
import com.example.tripwise_api.dto.SharedExpenseRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
public class ExpenseService {

    @Autowired
    private ExpenseDAL expenseDAL;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private ExpenseShareDAL expenseShareDAL;

    @Autowired
    private ExpenseCategoryService expenseCategoryService;

    @Autowired
    private TripParticipantService tripParticipantService;

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

        // Paid-by and split metadata (transient fields) are copied into persistent paidBy
        expense.setPaidBy(expenseDetails.getPaidBy());

        Expense saved = expenseDAL.saveExpense(expense);

        // After saving the expense, create ExpenseShare rows if requested
        createSharesForExpense(saved, expenseDetails);

        return saved;
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

        // Paid-by update
        if (expenseDetails.getPaidBy() != null) {
            expense.setPaidBy(expenseDetails.getPaidBy());
        }

        Expense saved = expenseDAL.saveExpense(expense);

        // NOTE: For simplicity we don't update shares on expense update in this change. This can be extended later.

        return saved;
    }


     // Convert expense amount to target currency

    private void convertExpenseCurrency(Expense expense, String targetCurrency) {
        if (!expense.getCurrency().equals(targetCurrency)) {
            BigDecimal exchangeRate = currencyService.getExchangeRate(expense.getCurrency(), targetCurrency);
            BigDecimal convertedAmount = expense.getOriginalAmount().multiply(exchangeRate);
            expense.setAmount(convertedAmount);
        }
    }

    /**
     * Create ExpenseShare rows for a newly saved expense based on transient split fields
     * Rules:
     * - If expenseDetails.splitWithUserIds is provided, shares will be created for the union of paidBy and splitWithUserIds
     * - If splitWithUserIds is empty or null, no shares are created (no split requested)
     * - If splitEvenly == true, the total amount is divided evenly across participants
     */
    private void createSharesForExpense(Expense savedExpense, Expense expenseDetails) {
        List<Long> splitWith = expenseDetails.getSplitWithUserIds();
        Boolean splitEvenly = expenseDetails.getSplitEvenly();
        Long paidBy = savedExpense.getPaidBy();

        if ((splitWith == null || splitWith.isEmpty())) {
            // nothing to do
            return;
        }

        // Build unique participant list including payer
        Set<Long> participantSet = new LinkedHashSet<>();
        if (paidBy != null) participantSet.add(paidBy);
        participantSet.addAll(splitWith);

        int participants = participantSet.size();
        if (participants == 0) return;

        // Only support even split for now
        BigDecimal total = savedExpense.getAmount() != null ? savedExpense.getAmount() : BigDecimal.ZERO;
        BigDecimal shareAmount = total.divide(new BigDecimal(participants), 2, RoundingMode.HALF_UP);

        for (Long userId : participantSet) {
            ExpenseShare share = new ExpenseShare(savedExpense.getExpenseId(), userId, shareAmount);
            expenseShareDAL.saveShare(share);
        }
    }

    public void deleteExpense(int id) {
        if (!expenseDAL.existsById(id)) {
            throw new IllegalArgumentException("Expense not found with id: " + id);
        }
        // Remove all shares for this expense before deleting the expense
        expenseShareDAL.deleteSharesByExpenseId(id);
        expenseDAL.deleteExpense(id);
    }

    /**
     * Create a shared expense from SharedExpenseRequest DTO using participant IDs
     * Converts the DTO to an Expense entity and creates shares for all participants
     */
    public Expense createSharedExpense(int tripId, SharedExpenseRequest request) {
        // Validate required fields
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Description is required");
        }
        if (request.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        if (request.getSplitWithParticipantIds() == null || request.getSplitWithParticipantIds().isEmpty()) {
            throw new IllegalArgumentException("Split participants are required");
        }

        // Get the expense category
        ExpenseCategory category = expenseCategoryService.getCategoryById((long) request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + request.getCategoryId()));

        // Get payer participant and validate
        TripParticipant payer = tripParticipantService.getParticipantById(request.getPaidByParticipantId())
                .orElseThrow(() -> new IllegalArgumentException("Payer participant not found with id: " + request.getPaidByParticipantId()));

        // Validate that payer belongs to this trip
        if (!payer.getTripId().equals(tripId)) {
            throw new IllegalArgumentException("Payer participant does not belong to this trip");
        }

        // Create expense entity
        Expense expense = new Expense();
        expense.setTripId(tripId);
        expense.setDescription(request.getDescription());
        expense.setAmount(BigDecimal.valueOf(request.getAmount()));
        expense.setOriginalAmount(BigDecimal.valueOf(request.getAmount()));
        expense.setCurrency(request.getCurrency() != null ? request.getCurrency() : "EUR");
        expense.setExchangeRate(BigDecimal.ONE);
        expense.setCategory(category);
        expense.setPaidBy(payer.getUserId()); // Store user ID in expense
        expense.setValidated(false);

        // Parse expense date
        if (request.getExpenseDate() != null && !request.getExpenseDate().trim().isEmpty()) {
            try {
                LocalDate expenseDate = LocalDate.parse(request.getExpenseDate(), DateTimeFormatter.ISO_LOCAL_DATE);
                expense.setExpenseDate(expenseDate);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid expense date format. Expected YYYY-MM-DD");
            }
        } else {
            expense.setExpenseDate(LocalDate.now());
        }

        // Save the expense first
        Expense saved = expenseDAL.saveExpense(expense);

        // Create shares for participants using participant IDs
        createSharesForParticipants(saved, request.getPaidByParticipantId(), request.getSplitWithParticipantIds(), request.isSplitEvenly());

        return saved;
    }

    /**
     * Create ExpenseShare rows for participants based on participant IDs
     */
    private void createSharesForParticipants(Expense savedExpense, int paidByParticipantId, List<Integer> splitWithParticipantIds, boolean splitEvenly) {
        if (splitWithParticipantIds == null || splitWithParticipantIds.isEmpty()) {
            return;
        }

        // Build unique participant list including payer
        Set<Integer> participantIds = new LinkedHashSet<>();
        participantIds.add(paidByParticipantId);
        participantIds.addAll(splitWithParticipantIds);

        // Get all participants and validate they belong to the trip
        Map<Integer, Long> participantIdToUserIdMap = new HashMap<>();
        for (Integer participantId : participantIds) {
            TripParticipant participant = tripParticipantService.getParticipantById(participantId)
                    .orElseThrow(() -> new IllegalArgumentException("Participant not found with id: " + participantId));

            if (!participant.getTripId().equals(savedExpense.getTripId())) {
                throw new IllegalArgumentException("Participant " + participantId + " does not belong to this trip");
            }

            participantIdToUserIdMap.put(participantId, participant.getUserId());
        }

        int participantCount = participantIds.size();
        if (participantCount == 0) return;

        // Calculate share amount (only support even split for now)
        BigDecimal total = savedExpense.getAmount() != null ? savedExpense.getAmount() : BigDecimal.ZERO;
        BigDecimal shareAmount = total.divide(new BigDecimal(participantCount), 2, RoundingMode.HALF_UP);

        // Create expense shares using user IDs
        for (Integer participantId : participantIds) {
            Long userId = participantIdToUserIdMap.get(participantId);
            ExpenseShare share = new ExpenseShare(savedExpense.getExpenseId(), userId, shareAmount);
            expenseShareDAL.saveShare(share);
        }
    }
}
