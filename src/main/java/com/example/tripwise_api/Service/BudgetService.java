package com.example.tripwise_api.Service;

import com.example.tripwise_api.Model.Budget;
import com.example.tripwise_api.Model.Trip;
import com.example.tripwise_api.DAL.BudgetDAL;
import com.example.tripwise_api.Repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BudgetService {

    @Autowired
    private BudgetDAL budgetDAL;

    @Autowired
    private TripRepository tripRepository;

    @Transactional(readOnly = true)
    public List<Budget> getAllBudgets() {
        return budgetDAL.getAllBudgets();
    }

    @Transactional(readOnly = true)
    public Optional<Budget> getBudgetById(int id) {
        return budgetDAL.getBudgetById(id);
    }

    @Transactional(readOnly = true)
    public Budget getBudgetByTripId(int tripId) {
        List<Budget> budgets = budgetDAL.getBudgetsByTripId(tripId);
        if (budgets.isEmpty()) {
            return null;
        }
        // Return the most recent budget (last in list)
        return budgets.get(budgets.size() - 1);
    }

    public Budget createBudget(int tripId, Budget budgetDetails) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found with id: " + tripId));

        // Delete existing budgets for this trip first
        List<Budget> existingBudgets = budgetDAL.getBudgetsByTripId(tripId);
        existingBudgets.forEach(budget -> budgetDAL.deleteBudget(budget.getBudgetId()));

        Budget budget = new Budget();
        budget.setTrip(trip);
        budget.setTotalBudget(budgetDetails.getTotalBudget());
        budget.setCurrency(budgetDetails.getCurrency());

        return budgetDAL.saveBudget(budget);
    }

    public Budget updateOrCreateBudget(int tripId, Budget budgetDetails) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found with id: " + tripId));

        List<Budget> budgets = budgetDAL.getBudgetsByTripId(tripId);
        Budget budget;

        if (!budgets.isEmpty()) {
            // Update the most recent budget
            budget = budgets.get(budgets.size() - 1);
            // Delete any other old budgets for this trip
            for (int i = 0; i < budgets.size() - 1; i++) {
                budgetDAL.deleteBudget(budgets.get(i).getBudgetId());
            }
        } else {
            // Create new budget
            budget = new Budget();
            budget.setTrip(trip);
        }

        budget.setTotalBudget(budgetDetails.getTotalBudget());
        budget.setCurrency(budgetDetails.getCurrency());

        return budgetDAL.saveBudget(budget);
    }

    public void deleteBudget(int tripId) {
        List<Budget> budgets = budgetDAL.getBudgetsByTripId(tripId);
        if (budgets.isEmpty()) {
            throw new IllegalArgumentException("No budget found for trip with id: " + tripId);
        }
        budgets.forEach(budget -> budgetDAL.deleteBudget(budget.getBudgetId()));
    }
}
