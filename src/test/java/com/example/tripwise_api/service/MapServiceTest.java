package com.example.tripwise_api.service;

import com.example.tripwise_api.dal.MapDAL;
import com.example.tripwise_api.dto.request.CreateMapRequest;
import com.example.tripwise_api.model.MapData;
import com.example.tripwise_api.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MapServiceTest {
    @Mock
    private MapDAL mapDAL;

    @InjectMocks
    private MapService mapService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testCreateMapForUser() {
        User user = new User();
        MapData map = new MapData();
        Long tripId = 1L;
        MapData savedMap = new MapData();
        when(mapDAL.saveMap(any(MapData.class))).thenReturn(savedMap);

        MapData result = mapService.createMapForUser(user, map, tripId);
        assertNotNull(result);
        verify(mapDAL).saveMap(map);
        assertEquals(user, map.getUser());
        assertEquals(tripId, map.getTripId());
    }

    @Test
    void testGetMapsForUser() {
        Long userId = 1L;
        List<MapData> maps = Arrays.asList(new MapData(), new MapData());
        when(mapDAL.getMapsByUserId(userId)).thenReturn(maps);

        List<MapData> result = mapService.getMapsForUser(userId);
        assertEquals(2, result.size());
        verify(mapDAL).getMapsByUserId(userId);
    }

    @Test
    void testGetMapsByTripId() {
        Long tripId = 1L;
        Long userId = 2L;
        List<MapData> maps = Collections.singletonList(new MapData());
        when(mapDAL.getMapsByTripIdAndUserId(tripId, userId)).thenReturn(maps);

        List<MapData> result = mapService.getMapsByTripId(tripId, userId);
        assertEquals(1, result.size());
        verify(mapDAL).getMapsByTripIdAndUserId(tripId, userId);
    }

    @Test
    void testGetMapById() {
        Long id = 1L;
        MapData map = new MapData();
        when(mapDAL.getMapById(id)).thenReturn(Optional.of(map));

        Optional<MapData> result = mapService.getMapById(id);
        assertTrue(result.isPresent());
        assertEquals(map, result.get());
        verify(mapDAL).getMapById(id);
    }

    @Test
    void testDeleteMap() {
        Long id = 1L;
        doNothing().when(mapDAL).deleteMap(id);
        mapService.deleteMap(id);
        verify(mapDAL).deleteMap(id);
    }

    @Test
    void testUpdateMap_Success() {
        Long id = 1L;
        CreateMapRequest request = new CreateMapRequest();
        MapData existingMap = new MapData();
        MapData updatedMap = new MapData();
        when(mapDAL.getMapById(id)).thenReturn(Optional.of(existingMap));
        when(mapDAL.saveMap(any(MapData.class))).thenReturn(updatedMap);

        MapData result = mapService.updateMap(id, request);
        assertNotNull(result);
        verify(mapDAL).getMapById(id);
        verify(mapDAL).saveMap(existingMap);
    }

    @Test
    void testUpdateMap_NotFound() {
        Long id = 1L;
        CreateMapRequest request = new CreateMapRequest();
        when(mapDAL.getMapById(id)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> mapService.updateMap(id, request));
        verify(mapDAL).getMapById(id);
    }
}
