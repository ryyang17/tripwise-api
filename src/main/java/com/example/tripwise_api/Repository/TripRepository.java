package com.example.tripwise_api.Repository;

import com.example.tripwise_api.Model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Integer> {
    // Custom query to find trips by user ID
    List<Trip> findByUserId(Long userId);
}