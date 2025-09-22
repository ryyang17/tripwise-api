package com.example.tripwise_api.Repository;

import com.example.tripwise_api.Model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByTripId(Long tripId);

    // Tel activiteiten per trip
    long countByTripId(Long tripId);

    // Vind activiteiten met geschatte kosten
    List<Activity> findByTripIdAndEstimatedCostIsNotNull(Long tripId);
}

