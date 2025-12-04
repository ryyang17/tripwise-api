package com.example.tripwise_api.dal;

import com.example.tripwise_api.model.ExpenseShare;
import com.example.tripwise_api.repository.ExpenseShareRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExpenseShareDAL {
    private final ExpenseShareRepository repository;

    public ExpenseShareDAL(ExpenseShareRepository repository) {
        this.repository = repository;
    }

    public ExpenseShare saveShare(ExpenseShare share) {
        return repository.save(share);
    }

    public List<ExpenseShare> getSharesByExpenseId(Integer expenseId) {
        return repository.findByExpenseId(expenseId);
    }

    // New method: Get shares with user details efficiently loaded
    public List<ExpenseShare> getSharesByExpenseIdWithUserDetails(Integer expenseId) {
        return repository.findByExpenseIdWithUser(expenseId);
    }

    public List<ExpenseShare> getSharesByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    // New method: Get unsettled shares for user
    public List<ExpenseShare> getUnsettledSharesByUserId(Long userId) {
        return repository.findUnsettledByUserId(userId);
    }

    public void deleteSharesByExpenseId(Integer expenseId) {
        repository.deleteByExpenseId(expenseId);
    }
}
