package com.example.tripwise_api.service;

import com.example.tripwise_api.dal.ExpenseShareDAL;
import com.example.tripwise_api.dto.mapper.ExpenseShareMapper;
import com.example.tripwise_api.model.ExpenseShare;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ExpenseShareService {

    private final ExpenseShareDAL expenseShareDAL;
    private final ExpenseShareMapper expenseShareMapper;
    private final TripParticipantService participantService;

    public ExpenseShareService(ExpenseShareDAL expenseShareDAL,
                             ExpenseShareMapper expenseShareMapper,
                             TripParticipantService participantService) {
        this.expenseShareDAL = expenseShareDAL;
        this.expenseShareMapper = expenseShareMapper;
        this.participantService = participantService;
    }

    /**
     * Create expense shares for multiple users with validation
     */
    @Transactional
    public List<ExpenseShare> createExpenseShares(Integer expenseId, Integer tripId,
                                                 List<Long> userIds, BigDecimal totalAmount) {
        // Validate all users are participants
        participantService.validateUsersAreParticipants(tripId, userIds);

        // Calculate equal shares
        BigDecimal shareAmount = totalAmount.divide(new BigDecimal(userIds.size()), 2, BigDecimal.ROUND_HALF_UP);

        List<ExpenseShare> shares = userIds.stream()
            .map(userId -> {
                ExpenseShare share = new ExpenseShare(expenseId, userId, shareAmount);
                return expenseShareDAL.saveShare(share);
            })
            .toList();

        return shares;
    }

    /**
     * Create custom expense shares with specific amounts per user
     */
    @Transactional
    public List<ExpenseShare> createCustomExpenseShares(Integer expenseId, Integer tripId,
                                                       Map<Long, BigDecimal> userAmounts) {
        // Validate all users are participants
        participantService.validateUsersAreParticipants(tripId, userAmounts.keySet().stream().toList());

        List<ExpenseShare> shares = userAmounts.entrySet().stream()
            .map(entry -> {
                ExpenseShare share = new ExpenseShare(expenseId, entry.getKey(), entry.getValue());
                return expenseShareDAL.saveShare(share);
            })
            .toList();

        return shares;
    }

    /**
     * Get expense shares with user details efficiently loaded
     */
    @Transactional(readOnly = true)
    public List<ExpenseShare> getExpenseSharesWithUserDetails(Integer expenseId) {
        return expenseShareDAL.getSharesByExpenseIdWithUserDetails(expenseId);
    }

    /**
     * Get expense shares as user details map
     */
    @Transactional(readOnly = true)
    public Map<Long, ExpenseShareMapper.ShareDetails> getExpenseShareDetailsMap(Integer expenseId) {
        List<ExpenseShare> shares = getExpenseSharesWithUserDetails(expenseId);
        return expenseShareMapper.toDetailedSharesMap(shares);
    }

    /**
     * Calculate outstanding balances for a user across all their unsettled shares
     */
    @Transactional(readOnly = true)
    public Map<Long, BigDecimal> calculateOutstandingBalances(Long userId) {
        List<ExpenseShare> unsettledShares = expenseShareDAL.getUnsettledSharesByUserId(userId);

        Map<Long, BigDecimal> balances = new HashMap<>();

        for (ExpenseShare share : unsettledShares) {
            // Note: We'd need to get the expense to find who paid
            // This could be enhanced with a more complex query joining expense and expense_share
            balances.merge(share.getUserId(), share.getAmount(), BigDecimal::add);
        }

        return balances;
    }

    /**
     * Mark expense share as settled
     */
    @Transactional
    public ExpenseShare settleExpenseShare(Integer shareId) {
        ExpenseShare share = expenseShareDAL.getSharesByExpenseId(shareId)
            .stream()
            .filter(s -> s.getId().equals(shareId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Share not found with id: " + shareId));

        share.setIsSettled(true);
        return expenseShareDAL.saveShare(share);
    }

    /**
     * Get all unsettled shares for a user
     */
    @Transactional(readOnly = true)
    public List<ExpenseShare> getUnsettledSharesForUser(Long userId) {
        return expenseShareDAL.getUnsettledSharesByUserId(userId);
    }

    /**
     * Get summary of user's share obligations
     */
    @Transactional(readOnly = true)
    public UserShareSummary getUserShareSummary(Long userId) {
        List<ExpenseShare> allShares = expenseShareDAL.getSharesByUserId(userId);
        List<ExpenseShare> unsettledShares = expenseShareDAL.getUnsettledSharesByUserId(userId);

        BigDecimal totalOwed = allShares.stream()
            .map(ExpenseShare::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalUnsettled = unsettledShares.stream()
            .map(ExpenseShare::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new UserShareSummary(userId, totalOwed, totalUnsettled,
                                   allShares.size(), unsettledShares.size());
    }

    /**
     * Delete all shares for an expense
     */
    @Transactional
    public void deleteSharesForExpense(Integer expenseId) {
        expenseShareDAL.deleteSharesByExpenseId(expenseId);
    }

    /**
     * DTO for user share summary
     */
    public static class UserShareSummary {
        private final Long userId;
        private final BigDecimal totalOwed;
        private final BigDecimal totalUnsettled;
        private final Integer totalSharesCount;
        private final Integer unsettledSharesCount;

        public UserShareSummary(Long userId, BigDecimal totalOwed, BigDecimal totalUnsettled,
                              Integer totalSharesCount, Integer unsettledSharesCount) {
            this.userId = userId;
            this.totalOwed = totalOwed;
            this.totalUnsettled = totalUnsettled;
            this.totalSharesCount = totalSharesCount;
            this.unsettledSharesCount = unsettledSharesCount;
        }

        // Getters
        public Long getUserId() { return userId; }
        public BigDecimal getTotalOwed() { return totalOwed; }
        public BigDecimal getTotalUnsettled() { return totalUnsettled; }
        public Integer getTotalSharesCount() { return totalSharesCount; }
        public Integer getUnsettledSharesCount() { return unsettledSharesCount; }
    }
}
