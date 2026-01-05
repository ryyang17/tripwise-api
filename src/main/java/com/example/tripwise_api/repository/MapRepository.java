package com.example.tripwise_api.repository;

import com.example.tripwise_api.model.MapData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MapRepository extends JpaRepository<MapData, Long> {
    List<MapData> findByUser_Id(Long userId);
    List<MapData> findByTripIdAndUser_Id(Long tripId, Long userId);
}

