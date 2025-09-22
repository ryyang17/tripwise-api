package com.example.tripwise_api.Repository;

import com.example.tripwise_api.Model.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {
    // Alleen actieve categorieën
    List<ExpenseCategory> findByIsActiveTrue();

    // Zoek op naam
    ExpenseCategory findByCategoryName(String categoryName);

    // Check of naam al bestaat
    boolean existsByCategoryName(String categoryName);
}