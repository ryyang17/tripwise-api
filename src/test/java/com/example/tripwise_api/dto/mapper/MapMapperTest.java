package com.example.tripwise_api.dto.mapper;

import com.example.tripwise_api.dto.request.CreateMapRequest;
import com.example.tripwise_api.dto.response.MapResponse;
import com.example.tripwise_api.model.MapData;
import com.example.tripwise_api.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MapMapperTest {

    private CreateMapRequest createRequest;
    private MapData mapData;

    @BeforeEach
    void setUp() {
        createRequest = new CreateMapRequest();
        createRequest.setProvider("Google Maps");
        createRequest.setWaypoints("[{\"lat\": 40.7128, \"lng\": -74.0060}, {\"lat\": 34.0522, \"lng\": -118.2437}]");
        createRequest.setDistance("2790 miles");
        createRequest.setDuration("41 hours");
        createRequest.setLocation("New York to Los Angeles");
        createRequest.setRawPayload("{\"routes\": [{\"summary\": \"I-80 W\"}]}");

        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        mapData = new MapData();
        mapData.setId(1L);
        mapData.setTripId(100L);
        mapData.setUser(testUser);
        mapData.setProvider("Google Maps");
        mapData.setWaypoints("[{\"lat\": 40.7128, \"lng\": -74.0060}, {\"lat\": 34.0522, \"lng\": -118.2437}]");
        mapData.setDistance("2790 miles");
        mapData.setDuration("41 hours");
        mapData.setLocation("New York to Los Angeles");
        mapData.setRawPayload("{\"routes\": [{\"summary\": \"I-80 W\"}]}");
        mapData.setCreatedDate(LocalDateTime.now());
    }

    @Test
    void toEntity_ShouldMapRequestToEntity() {
        // Act
        MapData result = MapMapper.toEntity(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Google Maps", result.getProvider());
        assertEquals("[{\"lat\": 40.7128, \"lng\": -74.0060}, {\"lat\": 34.0522, \"lng\": -118.2437}]", result.getWaypoints());
        assertEquals("2790 miles", result.getDistance());
        assertEquals("41 hours", result.getDuration());
        assertEquals("New York to Los Angeles", result.getLocation());
        assertEquals("{\"routes\": [{\"summary\": \"I-80 W\"}]}", result.getRawPayload());
    }

    @Test
    void toEntity_WithMinimalData_ShouldMapCorrectly() {
        // Arrange
        CreateMapRequest minimalRequest = new CreateMapRequest();
        minimalRequest.setProvider("Mapbox");
        minimalRequest.setRawPayload("{\"data\": \"test\"}");

        // Act
        MapData result = MapMapper.toEntity(minimalRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Mapbox", result.getProvider());
        assertEquals("{\"data\": \"test\"}", result.getRawPayload());
        assertNull(result.getWaypoints());
        assertNull(result.getDistance());
        assertNull(result.getDuration());
        assertNull(result.getLocation());
    }

    @Test
    void toEntity_WithNullOptionalFields_ShouldHandleGracefully() {
        // Arrange
        CreateMapRequest requestWithNulls = new CreateMapRequest();
        requestWithNulls.setProvider("OpenStreetMap");
        requestWithNulls.setWaypoints(null);
        requestWithNulls.setDistance(null);
        requestWithNulls.setDuration(null);
        requestWithNulls.setLocation(null);
        requestWithNulls.setRawPayload("{\"empty\": true}");

        // Act
        MapData result = MapMapper.toEntity(requestWithNulls);

        // Assert
        assertNotNull(result);
        assertEquals("OpenStreetMap", result.getProvider());
        assertNull(result.getWaypoints());
        assertNull(result.getDistance());
        assertNull(result.getDuration());
        assertNull(result.getLocation());
        assertEquals("{\"empty\": true}", result.getRawPayload());
    }

    @Test
    void toResponse_ShouldMapEntityToResponse() {
        // Act
        MapResponse result = MapMapper.toResponse(mapData);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(100L, result.getTripId());
        assertEquals("Google Maps", result.getProvider());
        assertEquals("[{\"lat\": 40.7128, \"lng\": -74.0060}, {\"lat\": 34.0522, \"lng\": -118.2437}]", result.getWaypoints());
        assertEquals("2790 miles", result.getDistance());
        assertEquals("41 hours", result.getDuration());
        assertEquals("New York to Los Angeles", result.getLocation());
        assertEquals("{\"routes\": [{\"summary\": \"I-80 W\"}]}", result.getRawPayload());
        assertNotNull(result.getCreatedDate());
    }

    @Test
    void toResponse_WithNullOptionalFields_ShouldMapCorrectly() {
        // Arrange
        mapData.setWaypoints(null);
        mapData.setDistance(null);
        mapData.setDuration(null);
        mapData.setLocation(null);

        // Act
        MapResponse result = MapMapper.toResponse(mapData);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNull(result.getWaypoints());
        assertNull(result.getDistance());
        assertNull(result.getDuration());
        assertNull(result.getLocation());
    }

    @Test
    void toResponseList_ShouldMapListOfEntities() {
        // Arrange
        MapData mapData2 = new MapData();
        mapData2.setId(2L);
        mapData2.setTripId(100L);
        mapData2.setProvider("Mapbox");
        mapData2.setWaypoints("[{\"lat\": 51.5074, \"lng\": -0.1278}]");
        mapData2.setDistance("0 miles");
        mapData2.setDuration("0 hours");
        mapData2.setLocation("London");
        mapData2.setRawPayload("{\"test\": \"data2\"}");
        mapData2.setCreatedDate(LocalDateTime.now());

        List<MapData> mapList = List.of(mapData, mapData2);

        // Act
        List<MapResponse> result = MapMapper.toResponseList(mapList);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        MapResponse response1 = result.get(0);
        assertEquals(1L, response1.getId());
        assertEquals("Google Maps", response1.getProvider());
        assertEquals("New York to Los Angeles", response1.getLocation());

        MapResponse response2 = result.get(1);
        assertEquals(2L, response2.getId());
        assertEquals("Mapbox", response2.getProvider());
        assertEquals("London", response2.getLocation());
    }

    @Test
    void toResponseList_WithEmptyList_ShouldReturnEmptyList() {
        // Act
        List<MapResponse> result = MapMapper.toResponseList(new ArrayList<>());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toResponseList_WithSingleItem_ShouldReturnListWithOneItem() {
        // Arrange
        List<MapData> singleItemList = List.of(mapData);

        // Act
        List<MapResponse> result = MapMapper.toResponseList(singleItemList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void toEntity_WithComplexWaypoints_ShouldMapCorrectly() {
        // Arrange
        String complexWaypoints = "[" +
                "{\"lat\": 40.7128, \"lng\": -74.0060, \"name\": \"New York\"}," +
                "{\"lat\": 41.8781, \"lng\": -87.6298, \"name\": \"Chicago\"}," +
                "{\"lat\": 34.0522, \"lng\": -118.2437, \"name\": \"Los Angeles\"}" +
                "]";
        createRequest.setWaypoints(complexWaypoints);

        // Act
        MapData result = MapMapper.toEntity(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals(complexWaypoints, result.getWaypoints());
    }

    @Test
    void toResponse_WithComplexRawPayload_ShouldMapCorrectly() {
        // Arrange
        String complexPayload = "{" +
                "\"routes\": [{" +
                "\"summary\": \"I-80 W\"," +
                "\"legs\": [{\"distance\": {\"value\": 4491274}}]," +
                "\"waypoint_order\": [0, 1]" +
                "}]," +
                "\"status\": \"OK\"" +
                "}";
        mapData.setRawPayload(complexPayload);

        // Act
        MapResponse result = MapMapper.toResponse(mapData);

        // Assert
        assertNotNull(result);
        assertEquals(complexPayload, result.getRawPayload());
    }

    @Test
    void toEntity_WithDifferentProviders_ShouldMapCorrectly() {
        // Test with different providers
        String[] providers = {"Google Maps", "Mapbox", "OpenStreetMap", "HERE Maps", "TomTom"};

        for (String provider : providers) {
            // Arrange
            createRequest.setProvider(provider);

            // Act
            MapData result = MapMapper.toEntity(createRequest);

            // Assert
            assertNotNull(result);
            assertEquals(provider, result.getProvider());
        }
    }

    @Test
    void toResponse_WithMultipleMaps_ShouldMaintainOrder() {
        // Arrange
        List<MapData> orderedMaps = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            MapData map = new MapData();
            map.setId((long) i);
            map.setTripId(100L);
            map.setProvider("Provider " + i);
            map.setRawPayload("{\"id\": " + i + "}");
            map.setCreatedDate(LocalDateTime.now());
            orderedMaps.add(map);
        }

        // Act
        List<MapResponse> result = MapMapper.toResponseList(orderedMaps);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.size());
        for (int i = 0; i < 5; i++) {
            assertEquals(i + 1L, result.get(i).getId());
            assertEquals("Provider " + (i + 1), result.get(i).getProvider());
        }
    }
}

