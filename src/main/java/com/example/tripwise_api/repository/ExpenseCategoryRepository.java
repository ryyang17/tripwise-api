package com.example.tripwise_api.repository;

import com.example.tripwise_api.model.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {
    // Alleen actieve categorieën
    List<ExpenseCategory> findByIsActiveTrue();

    // Zoek op naam
    Optional <ExpenseCategory> findByCategoryName(String categoryName);

    // Check of naam al bestaat
    boolean existsByCategoryName(String categoryName);
}