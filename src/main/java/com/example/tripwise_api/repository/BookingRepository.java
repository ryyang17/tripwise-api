package com.example.tripwise_api.repository;

import com.example.tripwise_api.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser_Id(Long userId);
    List<Booking> findByTripIdAndUser_Id(Long tripId, Long userId);
}
