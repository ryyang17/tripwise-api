package com.example.tripwise_api.Repository;

import com.example.tripwise_api.Model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Integer> {
    List<Trip> findByUserId(Long userId);
}