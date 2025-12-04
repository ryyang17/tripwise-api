package com.example.tripwise_api.repository;

import com.example.tripwise_api.model.ExpenseShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseShareRepository extends JpaRepository<ExpenseShare, Integer> {
    List<ExpenseShare> findByExpenseId(Integer expenseId);
    List<ExpenseShare> findByUserId(Long userId);
    void deleteByExpenseId(Integer expenseId);

    // Get all expense shares with user details eagerly loaded
    @Query("SELECT es FROM ExpenseShare es JOIN FETCH es.user WHERE es.expenseId = :expenseId")
    List<ExpenseShare> findByExpenseIdWithUser(@Param("expenseId") Integer expenseId);

    // Get all unsettled shares for a user
    @Query("SELECT es FROM ExpenseShare es WHERE es.userId = :userId AND es.isSettled = false")
    List<ExpenseShare> findUnsettledByUserId(@Param("userId") Long userId);
}
