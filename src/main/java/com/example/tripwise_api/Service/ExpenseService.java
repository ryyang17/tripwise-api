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
        List<Expense> expenses = expenseDAL.getExpensesByTripId(tripId);
        populateSharedExpenseFields(expenses);
        return expenses;
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

        populateSharedExpenseFields(expenses);
        return expenses;
    }

    @Transactional(readOnly = true)
    public Optional<Expense> getExpenseById(int id) {
        Optional<Expense> expenseOpt = expenseDAL.getExpenseById(id);
        if (expenseOpt.isPresent()) {
            populateSharedExpenseFields(expenseOpt.get());
        }
        return expenseOpt;
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
        createSharesForParticipants(saved, request.getPaidByParticipantId(), request.getSplitWithParticipantIds(), request.isSplitEvenly(), request.getCustomSplits());

        // Populate shared expense fields for frontend compatibility
        populateSharedExpenseFields(saved);

        return saved;
    }

    /**
     * Create ExpenseShare rows for participants based on participant IDs
     */
    private void createSharesForParticipants(Expense savedExpense, int paidByParticipantId, List<Integer> splitWithParticipantIds, boolean splitEvenly, Map<Integer, Double> customSplits) {
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

        // Create expense shares using custom splits if provided, otherwise split evenly
        if (!splitEvenly && customSplits != null && !customSplits.isEmpty()) {
            // Custom split: use provided amounts
            BigDecimal totalCustomAmount = BigDecimal.ZERO;

            for (Integer participantId : participantIds) {
                Double customAmount = customSplits.get(participantId);
                if (customAmount != null && customAmount > 0) {
                    BigDecimal shareAmount = BigDecimal.valueOf(customAmount);
                    totalCustomAmount = totalCustomAmount.add(shareAmount);

                    Long userId = participantIdToUserIdMap.get(participantId);
                    ExpenseShare share = new ExpenseShare(savedExpense.getExpenseId(), userId, shareAmount);
                    expenseShareDAL.saveShare(share);
                }
            }

            // Validate that custom splits add up to total expense amount (with small tolerance)
            BigDecimal totalExpense = savedExpense.getAmount() != null ? savedExpense.getAmount() : BigDecimal.ZERO;
            BigDecimal difference = totalExpense.subtract(totalCustomAmount).abs();
            if (difference.compareTo(new BigDecimal("0.01")) > 0) {
                throw new IllegalArgumentException("Custom split amounts (" + totalCustomAmount + ") do not add up to total expense amount (" + totalExpense + ")");
            }
        } else {
            // Even split: divide amount evenly across participants
            BigDecimal total = savedExpense.getAmount() != null ? savedExpense.getAmount() : BigDecimal.ZERO;
            BigDecimal shareAmount = total.divide(new BigDecimal(participantCount), 2, RoundingMode.HALF_UP);

            for (Integer participantId : participantIds) {
                Long userId = participantIdToUserIdMap.get(participantId);
                ExpenseShare share = new ExpenseShare(savedExpense.getExpenseId(), userId, shareAmount);
                expenseShareDAL.saveShare(share);
            }
        }
    }

    /**
     * Update a shared expense from SharedExpenseRequest DTO using participant IDs
     */
    public Expense updateSharedExpense(int tripId, int expenseId, SharedExpenseRequest request) {
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

        // Get existing expense and validate
        Expense existingExpense = expenseDAL.getExpenseById(expenseId)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found with id: " + expenseId));

        if (existingExpense.getTripId() != tripId) {
            throw new IllegalArgumentException("Expense does not belong to this trip");
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

        // Update expense fields
        existingExpense.setDescription(request.getDescription());
        existingExpense.setAmount(BigDecimal.valueOf(request.getAmount()));
        existingExpense.setOriginalAmount(BigDecimal.valueOf(request.getAmount()));
        existingExpense.setCurrency(request.getCurrency() != null ? request.getCurrency() : "EUR");
        existingExpense.setExchangeRate(BigDecimal.ONE);
        existingExpense.setCategory(category);
        existingExpense.setPaidBy(payer.getUserId()); // Store user ID in expense

        // Parse expense date
        if (request.getExpenseDate() != null && !request.getExpenseDate().trim().isEmpty()) {
            try {
                LocalDate expenseDate = LocalDate.parse(request.getExpenseDate(), DateTimeFormatter.ISO_LOCAL_DATE);
                existingExpense.setExpenseDate(expenseDate);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid expense date format. Expected YYYY-MM-DD");
            }
        }

        // Save the updated expense
        Expense saved = expenseDAL.saveExpense(existingExpense);

        // Remove existing shares and create new ones
        expenseShareDAL.deleteSharesByExpenseId(expenseId);
        createSharesForParticipants(saved, request.getPaidByParticipantId(), request.getSplitWithParticipantIds(), request.isSplitEvenly(), request.getCustomSplits());

        // Populate shared expense fields for frontend compatibility
        populateSharedExpenseFields(saved);

        return saved;
    }

    /**
     * Populate shared expense fields for frontend compatibility
     */
    private void populateSharedExpenseFields(Expense expense) {
        // Check if this expense has shares
        List<ExpenseShare> shares = expenseShareDAL.getSharesByExpenseId(expense.getExpenseId());

        if (shares != null && !shares.isEmpty()) {
            expense.setIsSharedExpense(true);

            // Convert user IDs back to participant IDs for frontend compatibility
            List<Integer> participantIds = new ArrayList<>();
            Map<Integer, Double> customSplits = new HashMap<>();
            Integer payerParticipantId = null;

            // Get all participants for this trip to create user->participant mapping
            List<TripParticipant> tripParticipants = tripParticipantService.getParticipantsByTripId(expense.getTripId());
            Map<Long, Integer> userIdToParticipantIdMap = new HashMap<>();

            for (TripParticipant participant : tripParticipants) {
                userIdToParticipantIdMap.put(participant.getUserId(), participant.getId());
            }

            // Check if all shares have the same amount (even split) or different amounts (custom split)
            BigDecimal firstShareAmount = null;
            boolean isEvenSplit = true;

            // Convert shares to participant IDs and build custom splits map
            for (ExpenseShare share : shares) {
                Integer participantId = userIdToParticipantIdMap.get(share.getUserId());
                if (participantId != null) {
                    participantIds.add(participantId);
                    customSplits.put(participantId, share.getAmount().doubleValue());

                    // Check if this is the payer
                    if (expense.getPaidBy() != null && expense.getPaidBy().equals(share.getUserId())) {
                        payerParticipantId = participantId;
                    }

                    // Check if all amounts are the same (even split)
                    if (firstShareAmount == null) {
                        firstShareAmount = share.getAmount();
                    } else if (!firstShareAmount.equals(share.getAmount())) {
                        isEvenSplit = false;
                    }
                }
            }

            expense.setPaidById(payerParticipantId);
            expense.setSplitWith(participantIds);
            expense.setSplitEvenly(isEvenSplit);

            // Always set custom splits - frontend can use this for editing
            expense.setCustomSplits(customSplits);
        } else {
            expense.setIsSharedExpense(false);
            expense.setPaidById(null);
            expense.setSplitWith(new ArrayList<>());
            expense.setSplitEvenly(true);
            expense.setCustomSplits(new HashMap<>());
        }
    }

    /**
     * Populate shared expense fields for a list of expenses
     */
    private void populateSharedExpenseFields(List<Expense> expenses) {
        for (Expense expense : expenses) {
            populateSharedExpenseFields(expense);
        }
    }
}
