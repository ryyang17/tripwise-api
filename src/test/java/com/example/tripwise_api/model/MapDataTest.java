package com.example.tripwise_api.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MapDataTest {

    private MapData mapData;
    private User user;

    @BeforeEach
    void setUp() {
        mapData = new MapData();
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
    }

    @Test
    void constructor_ShouldSetCreatedDate() {
        // Act
        MapData newMapData = new MapData();

        // Assert
        assertNotNull(newMapData.getCreatedDate());
    }

    @Test
    void setAndGetId_ShouldWorkCorrectly() {
        // Act
        mapData.setId(1L);

        // Assert
        assertEquals(1L, mapData.getId());
    }

    @Test
    void setAndGetUser_ShouldWorkCorrectly() {
        // Act
        mapData.setUser(user);

        // Assert
        assertEquals(user, mapData.getUser());
    }

    @Test
    void setAndGetTripId_ShouldWorkCorrectly() {
        // Act
        mapData.setTripId(10L);

        // Assert
        assertEquals(10L, mapData.getTripId());
    }

    @Test
    void setAndGetProvider_ShouldWorkCorrectly() {
        // Act
        mapData.setProvider("Google Maps");

        // Assert
        assertEquals("Google Maps", mapData.getProvider());
    }

    @Test
    void setAndGetWaypoints_ShouldWorkCorrectly() {
        // Arrange
        String waypoints = "[{\"lat\":40.7128,\"lng\":-74.0060},{\"lat\":34.0522,\"lng\":-118.2437}]";

        // Act
        mapData.setWaypoints(waypoints);

        // Assert
        assertEquals(waypoints, mapData.getWaypoints());
    }

    @Test
    void setAndGetDistance_ShouldWorkCorrectly() {
        // Act
        mapData.setDistance("2,451 miles");

        // Assert
        assertEquals("2,451 miles", mapData.getDistance());
    }

    @Test
    void setAndGetDuration_ShouldWorkCorrectly() {
        // Act
        mapData.setDuration("38 hours");

        // Assert
        assertEquals("38 hours", mapData.getDuration());
    }

    @Test
    void setAndGetLocation_ShouldWorkCorrectly() {
        // Act
        mapData.setLocation("New York, NY");

        // Assert
        assertEquals("New York, NY", mapData.getLocation());
    }

    @Test
    void setAndGetRawPayload_ShouldWorkCorrectly() {
        // Arrange
        String payload = "{\"status\":\"OK\",\"routes\":[{\"legs\":[]}]}";

        // Act
        mapData.setRawPayload(payload);

        // Assert
        assertEquals(payload, mapData.getRawPayload());
    }

    @Test
    void setAndGetCreatedDate_ShouldWorkCorrectly() {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.of(2026, 1, 8, 12, 30);

        // Act
        mapData.setCreatedDate(dateTime);

        // Assert
        assertEquals(dateTime, mapData.getCreatedDate());
    }

    @Test
    void waypoints_ShouldHandleEmptyJson() {
        // Act
        mapData.setWaypoints("[]");

        // Assert
        assertEquals("[]", mapData.getWaypoints());
    }

    @Test
    void waypoints_ShouldHandleComplexJson() {
        // Arrange
        String complexWaypoints = "[{\"lat\":40.7128,\"lng\":-74.0060,\"name\":\"New York\"},{\"lat\":34.0522,\"lng\":-118.2437,\"name\":\"Los Angeles\"}]";

        // Act
        mapData.setWaypoints(complexWaypoints);

        // Assert
        assertEquals(complexWaypoints, mapData.getWaypoints());
    }

    @Test
    void provider_ShouldHandleDifferentProviders() {
        // Arrange
        String[] providers = {"Google Maps", "Mapbox", "OpenStreetMap", "HERE Maps"};

        for (String provider : providers) {
            // Act
            mapData.setProvider(provider);

            // Assert
            assertEquals(provider, mapData.getProvider());
        }
    }

    @Test
    void distance_ShouldHandleVariousFormats() {
        // Arrange
        String[] distances = {"10 km", "100 miles", "5000 m", "3.5 kilometers"};

        for (String distance : distances) {
            // Act
            mapData.setDistance(distance);

            // Assert
            assertEquals(distance, mapData.getDistance());
        }
    }

    @Test
    void duration_ShouldHandleVariousFormats() {
        // Arrange
        String[] durations = {"2 hours", "45 minutes", "1h 30m", "90 min"};

        for (String duration : durations) {
            // Act
            mapData.setDuration(duration);

            // Assert
            assertEquals(duration, mapData.getDuration());
        }
    }

    @Test
    void rawPayload_ShouldHandleLargeJson() {
        // Arrange
        String largePayload = "{\"status\":\"OK\",\"routes\":[{\"legs\":[{\"distance\":{\"value\":100000},\"duration\":{\"value\":3600}}]}],\"geocoded_waypoints\":[{\"place_id\":\"123\"},{\"place_id\":\"456\"}]}";

        // Act
        mapData.setRawPayload(largePayload);

        // Assert
        assertEquals(largePayload, mapData.getRawPayload());
    }

    @Test
    void location_ShouldHandleNull() {
        // Act
        mapData.setLocation(null);

        // Assert
        assertNull(mapData.getLocation());
    }

    @Test
    void user_ShouldHandleNull() {
        // Act
        mapData.setUser(null);

        // Assert
        assertNull(mapData.getUser());
    }

    @Test
    void tripId_ShouldHandleNull() {
        // Act
        mapData.setTripId(null);

        // Assert
        assertNull(mapData.getTripId());
    }

    @Test
    void createdDate_ShouldBeInitializedByDefault() {
        // Act
        MapData newMapData = new MapData();

        // Assert
        assertNotNull(newMapData.getCreatedDate());
        assertTrue(newMapData.getCreatedDate().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}

