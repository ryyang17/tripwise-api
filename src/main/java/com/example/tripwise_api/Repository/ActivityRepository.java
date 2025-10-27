package com.example.tripwise_api.Repository;

import com.example.tripwise_api.Model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByTripId(Long tripId);

    // Tel activiteiten per trip
    long countByTripId(Long tripId);

    // Vind activiteiten met geschatte kosten
    List<Activity> findByTripIdAndEstimatedCostIsNotNull(Long tripId);

    // NEW: Simple method to get activities ordered by date and time
    @Query("SELECT a FROM Activity a WHERE a.tripId = :tripId ORDER BY " +
           "CASE WHEN a.date IS NULL THEN 1 ELSE 0 END, a.date ASC, " +
           "CASE WHEN a.time IS NULL THEN 1 ELSE 0 END, a.time ASC")
    List<Activity> findByTripIdOrderByDateAscTimeAsc(@Param("tripId") Long tripId);

    // Optional: Find activities for a specific date
    List<Activity> findByTripIdAndDate(Long tripId, LocalDate date);
}
