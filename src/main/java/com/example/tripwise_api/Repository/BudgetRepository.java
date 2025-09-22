package com.example.tripwise_api.Repository;

import com.example.tripwise_api.Model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer> {
    Optional<Budget> findByTripId(int tripId);
    void deleteByTripId(int tripId);
}
