package com.example.tripwise_api.dal;

import com.example.tripwise_api.model.MapData;
import com.example.tripwise_api.model.User;
import com.example.tripwise_api.repository.MapRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MapDALTest {

    @Mock
    private MapRepository mapRepository;

    @InjectMocks
    private MapDAL mapDAL;

    private MapData testMap;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        testMap = new MapData();
        testMap.setId(1L);
        testMap.setTripId(100L);
        testMap.setUser(testUser);
        testMap.setProvider("GoogleMaps");
        testMap.setWaypoints("[{\"lat\": 52.3676, \"lng\": 4.9041}]");
        testMap.setDistance("10 km");
        testMap.setDuration("15 mins");
        testMap.setLocation("Amsterdam");
        testMap.setRawPayload("{\"routes\": []}");
        testMap.setCreatedDate(LocalDateTime.now());
    }

    @Test
    void saveMap_ShouldSaveAndReturnMap() {
        // Arrange
        when(mapRepository.save(any(MapData.class))).thenReturn(testMap);

        // Act
        MapData result = mapDAL.saveMap(testMap);

        // Assert
        assertNotNull(result);
        assertEquals(testMap, result);
        assertEquals("GoogleMaps", result.getProvider());
        assertEquals("Amsterdam", result.getLocation());
        verify(mapRepository, times(1)).save(testMap);
    }

    @Test
    void saveMap_WithNewMap_ShouldSaveSuccessfully() {
        // Arrange
        MapData newMap = new MapData();
        newMap.setId(2L);
        newMap.setTripId(200L);
        newMap.setProvider("MapBox");
        newMap.setRawPayload("{\"data\": \"test\"}");
        when(mapRepository.save(any(MapData.class))).thenReturn(newMap);

        // Act
        MapData result = mapDAL.saveMap(newMap);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("MapBox", result.getProvider());
        verify(mapRepository, times(1)).save(newMap);
    }

    @Test
    void getMapById_WhenExists_ShouldReturnMap() {
        // Arrange
        when(mapRepository.findById(1L)).thenReturn(Optional.of(testMap));

        // Act
        Optional<MapData> result = mapDAL.getMapById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testMap, result.get());
        assertEquals("GoogleMaps", result.get().getProvider());
        verify(mapRepository, times(1)).findById(1L);
    }

    @Test
    void getMapById_WhenNotExists_ShouldReturnEmpty() {
        // Arrange
        when(mapRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<MapData> result = mapDAL.getMapById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(mapRepository, times(1)).findById(999L);
    }

    @Test
    void getMapsByUserId_ShouldReturnUserMaps() {
        // Arrange
        MapData map2 = new MapData();
        map2.setId(2L);
        map2.setUser(testUser);
        map2.setProvider("MapBox");
        List<MapData> expectedMaps = List.of(testMap, map2);
        when(mapRepository.findByUser_Id(1L)).thenReturn(expectedMaps);

        // Act
        List<MapData> result = mapDAL.getMapsByUserId(1L);

        // Assert
        assertEquals(expectedMaps, result);
        assertEquals(2, result.size());
        verify(mapRepository, times(1)).findByUser_Id(1L);
    }

    @Test
    void getMapsByUserId_WhenNoMaps_ShouldReturnEmptyList() {
        // Arrange
        when(mapRepository.findByUser_Id(999L)).thenReturn(List.of());

        // Act
        List<MapData> result = mapDAL.getMapsByUserId(999L);

        // Assert
        assertTrue(result.isEmpty());
        verify(mapRepository, times(1)).findByUser_Id(999L);
    }

    @Test
    void getMapsByTripIdAndUserId_ShouldReturnFilteredMaps() {
        // Arrange
        List<MapData> expectedMaps = List.of(testMap);
        when(mapRepository.findByTripIdAndUser_Id(100L, 1L)).thenReturn(expectedMaps);

        // Act
        List<MapData> result = mapDAL.getMapsByTripIdAndUserId(100L, 1L);

        // Assert
        assertEquals(expectedMaps, result);
        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getTripId());
        verify(mapRepository, times(1)).findByTripIdAndUser_Id(100L, 1L);
    }

    @Test
    void getMapsByTripIdAndUserId_WhenNoMaps_ShouldReturnEmptyList() {
        // Arrange
        when(mapRepository.findByTripIdAndUser_Id(999L, 999L)).thenReturn(List.of());

        // Act
        List<MapData> result = mapDAL.getMapsByTripIdAndUserId(999L, 999L);

        // Assert
        assertTrue(result.isEmpty());
        verify(mapRepository, times(1)).findByTripIdAndUser_Id(999L, 999L);
    }

    @Test
    void getMapsByTripIdAndUserId_WithDifferentTrip_ShouldReturnEmptyList() {
        // Arrange
        when(mapRepository.findByTripIdAndUser_Id(200L, 1L)).thenReturn(List.of());

        // Act
        List<MapData> result = mapDAL.getMapsByTripIdAndUserId(200L, 1L);

        // Assert
        assertTrue(result.isEmpty());
        verify(mapRepository, times(1)).findByTripIdAndUser_Id(200L, 1L);
    }

    @Test
    void deleteMap_ShouldDeleteMap() {
        // Arrange
        doNothing().when(mapRepository).deleteById(1L);

        // Act
        mapDAL.deleteMap(1L);

        // Assert
        verify(mapRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteMap_WithNonExistentId_ShouldStillInvokeDelete() {
        // Arrange
        doNothing().when(mapRepository).deleteById(999L);

        // Act
        mapDAL.deleteMap(999L);

        // Assert
        verify(mapRepository, times(1)).deleteById(999L);
    }
}

