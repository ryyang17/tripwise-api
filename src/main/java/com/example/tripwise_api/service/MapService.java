package com.example.tripwise_api.service;

import com.example.tripwise_api.dal.MapDAL;
import com.example.tripwise_api.dto.mapper.MapMapper;
import com.example.tripwise_api.dto.request.CreateMapRequest;
import com.example.tripwise_api.model.MapData;
import com.example.tripwise_api.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MapService {

    private final MapDAL mapDAL;

    public MapService(MapDAL mapDAL) {
        this.mapDAL = mapDAL;
    }

    public MapData createMapForUser(User user, MapData map, Long tripId) {
        map.setUser(user);
        map.setTripId(tripId);
        return mapDAL.saveMap(map);
    }

    @Transactional(readOnly = true)
    public List<MapData> getMapsForUser(Long userId) {
        return mapDAL.getMapsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<MapData> getMapsByTripId(Long tripId, Long userId) {
        return mapDAL.getMapsByTripIdAndUserId(tripId, userId);
    }

    @Transactional(readOnly = true)
    public Optional<MapData> getMapById(Long id) {
        return mapDAL.getMapById(id);
    }

    public void deleteMap(Long id) {
        mapDAL.deleteMap(id);
    }

    public MapData updateMap(Long id, CreateMapRequest request) {
        MapData existingMap = mapDAL.getMapById(id)
                .orElseThrow(() -> new IllegalArgumentException("Map not found with id: " + id));

        // Update fields from request
        MapData updatedData = MapMapper.toEntity(request);
        existingMap.setProvider(updatedData.getProvider());
        existingMap.setWaypoints(updatedData.getWaypoints());
        existingMap.setDistance(updatedData.getDistance());
        existingMap.setDuration(updatedData.getDuration());
        existingMap.setLocation(updatedData.getLocation());
        existingMap.setRawPayload(updatedData.getRawPayload());

        return mapDAL.saveMap(existingMap);
    }
}

