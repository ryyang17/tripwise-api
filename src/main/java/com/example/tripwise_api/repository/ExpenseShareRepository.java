package com.example.tripwise_api.repository;

import com.example.tripwise_api.model.ExpenseShare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseShareRepository extends JpaRepository<ExpenseShare, Integer> {
    List<ExpenseShare> findByExpenseId(Integer expenseId);
    List<ExpenseShare> findByUserId(Long userId);
    void deleteByExpenseId(Integer expenseId);
}

