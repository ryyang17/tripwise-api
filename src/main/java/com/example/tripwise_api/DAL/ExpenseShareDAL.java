package com.example.tripwise_api.DAL;

import com.example.tripwise_api.Model.ExpenseShare;
import com.example.tripwise_api.Repository.ExpenseShareRepository;
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

    public List<ExpenseShare> getSharesByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    public void deleteSharesByExpenseId(Integer expenseId) {
        repository.deleteByExpenseId(expenseId);
    }
}
