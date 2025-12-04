package com.example.tripwise_api.dto.mapper;

import com.example.tripwise_api.model.ExpenseShare;
import com.example.tripwise_api.model.User;
import com.example.tripwise_api.service.UserService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Mapper voor het converteren van ExpenseShare entities naar DTOs
 */
@Component
public class ExpenseShareMapper {

    private final UserService userService;

    public ExpenseShareMapper(UserService userService) {
        this.userService = userService;
    }

    /**
     * Convert een lijst van ExpenseShare entities naar een Map van userId -> amount
     */
    public Map<Long, BigDecimal> toUserSharesMap(List<ExpenseShare> shares) {
        if (shares == null) return new HashMap<>();

        Map<Long, BigDecimal> result = new HashMap<>();
        for (ExpenseShare share : shares) {
            result.put(share.getUserId(), share.getAmount());
        }
        return result;
    }

    /**
     * Convert een lijst van ExpenseShare entities naar een Map van userId -> ShareDetails
     * met uitgebreide informatie zoals username en settled status
     */
    public Map<Long, ShareDetails> toDetailedSharesMap(List<ExpenseShare> shares) {
        if (shares == null) return new HashMap<>();

        Map<Long, ShareDetails> result = new HashMap<>();
        for (ExpenseShare share : shares) {
            ShareDetails details = new ShareDetails();
            details.setUserId(share.getUserId());
            details.setAmount(share.getAmount());
            details.setIsSettled(share.getIsSettled());

            // Use the User relationship from ExpenseShare if available
            User user = share.getUser();
            if (user == null) {
                // Fallback to fetching user via UserService
                Optional<User> userOpt = userService.getUserById(share.getUserId());
                if (userOpt.isPresent()) {
                    user = userOpt.get();
                }
            }

            if (user != null) {
                details.setUserName(user.getUsername());
                details.setUserEmail(user.getEmail());
            }

            result.put(share.getUserId(), details);
        }
        return result;
    }

    /**
     * Inner class voor uitgebreide share details
     */
    public static class ShareDetails {
        private Long userId;
        private String userName;
        private String userEmail;
        private BigDecimal amount;
        private Boolean isSettled;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }

        public String getUserEmail() { return userEmail; }
        public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public Boolean getIsSettled() { return isSettled; }
        public void setIsSettled(Boolean isSettled) { this.isSettled = isSettled; }
    }
}

