package com.example.tripwise_api.service;

import com.example.tripwise_api.model.Expense;
import com.example.tripwise_api.model.ExpenseShare;
import com.example.tripwise_api.model.ExpenseCategory;
import com.example.tripwise_api.model.TripParticipant;
import com.example.tripwise_api.model.User;
import com.example.tripwise_api.dal.ExpenseDAL;
import com.example.tripwise_api.dal.ExpenseShareDAL;
import com.example.tripwise_api.dto.request.SharedExpenseRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class ExpenseService {
    private final ExpenseDAL expenseDAL;
    private final CurrencyService currencyService;
    private final ExpenseShareDAL expenseShareDAL;
    private final ExpenseCategoryService expenseCategoryService;
    private final TripParticipantService tripParticipantService;
    private final UserService userService;

    public ExpenseService(
        ExpenseDAL expenseDAL,
        CurrencyService currencyService,
        ExpenseShareDAL expenseShareDAL,
        ExpenseCategoryService expenseCategoryService,
        TripParticipantService tripParticipantService,
        UserService userService
    ) {
        this.expenseDAL = expenseDAL;
        this.currencyService = currencyService;
        this.expenseShareDAL = expenseShareDAL;
        this.expenseCategoryService = expenseCategoryService;
        this.tripParticipantService = tripParticipantService;
        this.userService = userService;
    }

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
        return expenseDAL.getExpenseById(id)
            .map(expense -> {
                populateSharedExpenseFields(expense);
                return expense;
            });
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

    // Create expense with automatic currency conversion
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

        Expense updated = expenseDAL.saveExpense(expense);

        // Remove all shares for this expense before recreating them
        expenseShareDAL.deleteSharesByExpenseId(id);
        createSharesForExpense(updated, expenseDetails);
        populateSharedExpenseFields(updated);

        return updated;
    }

    // Convert expense amount to target currency
    private void convertExpenseCurrency(Expense expense, String targetCurrency) {
        if (!expense.getCurrency().equals(targetCurrency)) {
            BigDecimal exchangeRate = currencyService.getExchangeRate(expense.getCurrency(), targetCurrency);
            BigDecimal convertedAmount = expense.getOriginalAmount().multiply(exchangeRate);
            expense.setAmount(convertedAmount);
        }
    }

    // Create ExpenseShare rows for an expense based on splitWithUserIds
    private void createSharesForExpense(Expense savedExpense, Expense expenseDetails) {
        List<Long> splitWith = expenseDetails.getSplitWithUserIds();
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
            // Fetch the User entity to properly set the relationship
            User user = userService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

            ExpenseShare share = new ExpenseShare(savedExpense.getExpenseId(), userId, shareAmount);
            share.setUser(user); // Set the User object to satisfy the @ManyToOne(nullable = false) constraint
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

    // Create a shared expense from SharedExpenseRequest DTO using participant ID
    public Expense createSharedExpense(int tripId, SharedExpenseRequest request) {
        validateSharedExpenseRequest(request);

        ExpenseCategory category = expenseCategoryService.getCategoryById((long) request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + request.getCategoryId()));

        TripParticipant payer = tripParticipantService.getParticipantById(request.getPaidByParticipantId())
                .orElseThrow(() -> new IllegalArgumentException("Payer participant not found with id: " + request.getPaidByParticipantId()));

        if (!payer.getTripId().equals(tripId)) {
            throw new IllegalArgumentException("Payer participant does not belong to this trip");
        }

        Expense expense = new Expense();
        expense.setTripId(tripId);
        mapRequestToExpense(expense, request, category, payer.getUserId());
        setExpenseDate(expense, request.getExpenseDate());

        Expense saved = expenseDAL.saveExpense(expense);
        createSharesForParticipants(saved, request.getPaidByParticipantId(), request.getSplitWithParticipantIds(),
                request.getSplitEvenly() != null ? request.getSplitEvenly() : false,
                convertCustomSplits(request.getCustomSplits()));
        populateSharedExpenseFields(saved);
        return saved;
    }

    public Expense updateSharedExpense(int tripId, int expenseId, SharedExpenseRequest request) {
        validateSharedExpenseRequest(request);

        Expense existingExpense = expenseDAL.getExpenseById(expenseId)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found with id: " + expenseId));

        if (existingExpense.getTripId() != tripId) {
            throw new IllegalArgumentException("Expense does not belong to this trip");
        }

        ExpenseCategory category = expenseCategoryService.getCategoryById((long) request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + request.getCategoryId()));

        TripParticipant payer = tripParticipantService.getParticipantById(request.getPaidByParticipantId())
                .orElseThrow(() -> new IllegalArgumentException("Payer participant not found with id: " + request.getPaidByParticipantId()));

        if (!payer.getTripId().equals(tripId)) {
            throw new IllegalArgumentException("Payer participant does not belong to this trip");
        }

        mapRequestToExpense(existingExpense, request, category, payer.getUserId());
        setExpenseDate(existingExpense, request.getExpenseDate());
        Expense saved = expenseDAL.saveExpense(existingExpense);
        Map<Integer, Double> convertedCustomSplits = convertCustomSplits(request.getCustomSplits());
        expenseShareDAL.deleteSharesByExpenseId(expenseId);
        createSharesForParticipants(saved, request.getPaidByParticipantId(), request.getSplitWithParticipantIds(),
                                   request.getSplitEvenly() != null ? request.getSplitEvenly() : false,
                                   convertedCustomSplits);
        populateSharedExpenseFields(saved);
        return saved;
    }

    // Create ExpenseShare rows for a shared expense based on splitWithParticipantIds
    private void createSharesForParticipants(Expense savedExpense, int paidByParticipantId, List<Integer> splitWithParticipantIds, boolean splitEvenly, Map<Integer, Double> customSplits) {
        if (splitWithParticipantIds == null || splitWithParticipantIds.isEmpty()) {
            return;
        }

        Set<Integer> participantIds = new LinkedHashSet<>();
        participantIds.add(paidByParticipantId);
        participantIds.addAll(splitWithParticipantIds);

        Map<Integer, Long> participantIdToUserIdMap = validateAndMapParticipants(participantIds, savedExpense.getTripId());
        int participantCount = participantIds.size();
        if (participantCount == 0) return;

        if (!splitEvenly && customSplits != null && !customSplits.isEmpty()) {
            handleCustomSplits(savedExpense, participantIds, participantIdToUserIdMap, customSplits);
        } else {
            handleEvenSplits(savedExpense, participantIds, participantIdToUserIdMap);
        }
    }

    private Map<Integer, Long> validateAndMapParticipants(Set<Integer> participantIds, int tripId) {
        Map<Integer, Long> map = new HashMap<>();
        for (Integer participantId : participantIds) {
            TripParticipant participant = tripParticipantService.getParticipantById(participantId)
                    .orElseThrow(() -> new IllegalArgumentException("Participant not found with id: " + participantId));
            if (!participant.getTripId().equals(tripId)) {
                throw new IllegalArgumentException("Participant " + participantId + " does not belong to this trip");
            }
            map.put(participantId, participant.getUserId());
        }
        return map;
    }

    private void handleCustomSplits(Expense savedExpense, Set<Integer> participantIds, Map<Integer, Long> participantIdToUserIdMap, Map<Integer, Double> customSplits) {
        BigDecimal totalCustomAmount = BigDecimal.ZERO;
        for (Integer participantId : participantIds) {
            Double customAmount = customSplits.get(participantId);
            if (customAmount != null && customAmount > 0) {
                BigDecimal shareAmount = BigDecimal.valueOf(customAmount);
                totalCustomAmount = totalCustomAmount.add(shareAmount);
                Long userId = participantIdToUserIdMap.get(participantId);

                // Fetch the User entity to properly set the relationship
                User user = userService.getUserById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

                ExpenseShare share = new ExpenseShare(savedExpense.getExpenseId(), userId, shareAmount);
                share.setUser(user); // Set the User object to satisfy the @ManyToOne(nullable = false) constraint
                expenseShareDAL.saveShare(share);
            }
        }
        BigDecimal totalExpense = savedExpense.getAmount() != null ? savedExpense.getAmount() : BigDecimal.ZERO;
        BigDecimal difference = totalExpense.subtract(totalCustomAmount).abs();
        if (difference.compareTo(new BigDecimal("0.01")) > 0) {
            throw new IllegalArgumentException("Custom split amounts (" + totalCustomAmount + ") do not add up to total expense amount (" + totalExpense + ")");
        }
    }

    private void handleEvenSplits(Expense savedExpense, Set<Integer> participantIds, Map<Integer, Long> participantIdToUserIdMap) {
        BigDecimal total = savedExpense.getAmount() != null ? savedExpense.getAmount() : BigDecimal.ZERO;
        BigDecimal shareAmount = total.divide(new BigDecimal(participantIds.size()), 2, RoundingMode.HALF_UP);
        for (Integer participantId : participantIds) {
            Long userId = participantIdToUserIdMap.get(participantId);

            // Fetch the User entity to properly set the relationship
            User user = userService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

            ExpenseShare share = new ExpenseShare(savedExpense.getExpenseId(), userId, shareAmount);
            share.setUser(user); // Set the User object to satisfy the @ManyToOne(nullable = false) constraint
            expenseShareDAL.saveShare(share);
        }
    }

    private void mapRequestToExpense(Expense expense, SharedExpenseRequest request, ExpenseCategory category, Long paidByUserId) {
        expense.setDescription(request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setOriginalAmount(request.getAmount());
        expense.setCurrency(request.getCurrency() != null ? request.getCurrency() : "EUR");
        expense.setExchangeRate(BigDecimal.ONE);
        expense.setCategory(category);
        expense.setPaidBy(paidByUserId);
        expense.setValidated(false);
    }

    private void setExpenseDate(Expense expense, LocalDate date) {
        expense.setExpenseDate(Objects.requireNonNullElseGet(date, LocalDate::now));
    }

    private Map<Integer, Double> convertCustomSplits(Map<String, BigDecimal> customSplits) {
        if (customSplits == null || customSplits.isEmpty()) return Collections.emptyMap();
        Map<Integer, Double> converted = new HashMap<>();
        for (Map.Entry<String, BigDecimal> entry : customSplits.entrySet()) {
            converted.put(Integer.parseInt(entry.getKey()), entry.getValue().doubleValue());
        }
        return converted;
    }

    private void validateSharedExpenseRequest(SharedExpenseRequest request) {
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Description is required");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        if (request.getSplitWithParticipantIds() == null || request.getSplitWithParticipantIds().isEmpty()) {
            throw new IllegalArgumentException("Split participants are required");
        }
    }

    private void populateSharedExpenseFields(Expense expense) {
        // Check if this expense has shares - use efficient method with user details
        List<ExpenseShare> shares = expenseShareDAL.getSharesByExpenseIdWithUserDetails(expense.getExpenseId());

        if (shares != null && !shares.isEmpty()) {
            expense.setIsSharedExpense(true);

            // Convert user IDs back to participant IDs for frontend compatibility
            List<Integer> participantIds = new ArrayList<>();
            Map<Integer, Double> customSplits = new HashMap<>();
            Integer payerParticipantId = null;

            // Get all participants with user details efficiently loaded
            List<TripParticipant> tripParticipants = tripParticipantService.getParticipantsByTripIdWithUserDetails(expense.getTripId());
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


    private void populateSharedExpenseFields(List<Expense> expenses) {
        for (Expense expense : expenses) {
            populateSharedExpenseFields(expense);
        }
    }
}
