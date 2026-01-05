package com.example.tripwise_api.dal;

import com.example.tripwise_api.model.MapData;
import com.example.tripwise_api.repository.MapRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class MapDAL {
    private final MapRepository mapRepository;

    public MapDAL(MapRepository mapRepository) {
        this.mapRepository = mapRepository;
    }

    public MapData saveMap(MapData map) {
        return mapRepository.save(map);
    }

    public Optional<MapData> getMapById(Long id) {
        return mapRepository.findById(id);
    }

    public List<MapData> getMapsByUserId(Long userId) {
        return mapRepository.findByUser_Id(userId);
    }

    public List<MapData> getMapsByTripIdAndUserId(Long tripId, Long userId) {
        return mapRepository.findByTripIdAndUser_Id(tripId, userId);
    }

    public void deleteMap(Long id) {
        mapRepository.deleteById(id);
    }
}

