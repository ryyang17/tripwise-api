package com.example.tripwise_api.controller;

import com.example.tripwise_api.dto.request.CreateMapRequest;
import com.example.tripwise_api.dto.response.MapResponse;
import com.example.tripwise_api.model.MapData;
import com.example.tripwise_api.model.User;
import com.example.tripwise_api.service.MapService;
import com.example.tripwise_api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MapControllerTest {

    @Mock
    private MapService mapService;

    @Mock
    private UserService userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private MapController mapController;

    private User testUser;
    private User otherUser;
    private MapData testMapData;
    private CreateMapRequest createRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setUsername("otheruser");
        otherUser.setEmail("other@example.com");

        testMapData = new MapData();
        testMapData.setId(1L);
        testMapData.setTripId(1L);
        testMapData.setUser(testUser);
        testMapData.setProvider("GoogleMaps");
        testMapData.setWaypoints("[{\"lat\": 52.3676, \"lng\": 4.9041}]");
        testMapData.setDistance("10 km");
        testMapData.setDuration("15 mins");
        testMapData.setLocation("Amsterdam");
        testMapData.setRawPayload("{\"routes\": []}");
        testMapData.setCreatedDate(LocalDateTime.now());

        createRequest = new CreateMapRequest();
        createRequest.setProvider("GoogleMaps");
        createRequest.setWaypoints("[{\"lat\": 52.3676, \"lng\": 4.9041}]");
        createRequest.setDistance("10 km");
        createRequest.setDuration("15 mins");
        createRequest.setLocation("Amsterdam");
        createRequest.setRawPayload("{\"routes\": []}");
    }

    private void setupAuthentication(boolean authenticated) {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(authenticated);
        if (authenticated) {
            lenient().when(authentication.getPrincipal()).thenReturn(testUser);
            when(authentication.getName()).thenReturn("testuser");
        } else {
            lenient().when(authentication.getPrincipal()).thenReturn("anonymousUser");
        }
    }

    private void setupAuthenticationNull() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);
    }

    // ========== Tests for createMap ==========

    @Test
    void createMap_WithValidData_ShouldReturnCreated() {
        // Arrange
        setupAuthentication(true);
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(mapService.createMapForUser(any(User.class), any(MapData.class), eq(1L)))
            .thenReturn(testMapData);

        // Act
        ResponseEntity<?> response = mapController.createMap(1L, createRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(MapResponse.class, response.getBody());
        verify(mapService, times(1)).createMapForUser(any(User.class), any(MapData.class), eq(1L));
    }

    @Test
    void createMap_WithoutAuthentication_ShouldReturnUnauthorized() {
        // Arrange
        setupAuthentication(false);

        // Act
        ResponseEntity<?> response = mapController.createMap(1L, createRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Not authenticated", response.getBody());
        verify(mapService, never()).createMapForUser(any(), any(), anyLong());
    }

    @Test
    void createMap_WithNullAuthentication_ShouldReturnUnauthorized() {
        // Arrange
        setupAuthenticationNull();

        // Act
        ResponseEntity<?> response = mapController.createMap(1L, createRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Not authenticated", response.getBody());
        verify(mapService, never()).createMapForUser(any(), any(), anyLong());
    }

    @Test
    void createMap_WithAnonymousUser_ShouldReturnUnauthorized() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        // Act
        ResponseEntity<?> response = mapController.createMap(1L, createRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Not authenticated", response.getBody());
        verify(mapService, never()).createMapForUser(any(), any(), anyLong());
    }

    @Test
    void createMap_WithUserNotFound_ShouldReturnNotFound() {
        // Arrange
        setupAuthentication(true);
        when(userService.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = mapController.createMap(1L, createRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
        verify(mapService, never()).createMapForUser(any(), any(), anyLong());
    }

    @Test
    void createMap_WithNullRawPayload_ShouldReturnBadRequest() {
        // Arrange
        setupAuthentication(true);
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        createRequest.setRawPayload(null);

        // Act
        ResponseEntity<?> response = mapController.createMap(1L, createRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("rawPayload is required", response.getBody());
        verify(mapService, never()).createMapForUser(any(), any(), anyLong());
    }

    @Test
    void createMap_WithBlankRawPayload_ShouldReturnBadRequest() {
        // Arrange
        setupAuthentication(true);
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        createRequest.setRawPayload("   ");

        // Act
        ResponseEntity<?> response = mapController.createMap(1L, createRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("rawPayload is required", response.getBody());
        verify(mapService, never()).createMapForUser(any(), any(), anyLong());
    }

    @Test
    void createMap_WithIllegalArgumentException_ShouldReturnBadRequest() {
        // Arrange
        setupAuthentication(true);
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(mapService.createMapForUser(any(User.class), any(MapData.class), eq(1L)))
            .thenThrow(new IllegalArgumentException("Invalid trip ID"));

        // Act
        ResponseEntity<?> response = mapController.createMap(1L, createRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid trip ID", response.getBody());
    }

    @Test
    void createMap_WithGenericException_ShouldReturnInternalServerError() {
        // Arrange
        setupAuthentication(true);
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(mapService.createMapForUser(any(User.class), any(MapData.class), eq(1L)))
            .thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<?> response = mapController.createMap(1L, createRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to save map", response.getBody());
    }

    // ========== Tests for getMapsByTrip ==========

    @Test
    void getMapsByTrip_WithAuthentication_ShouldReturnMaps() {
        // Arrange
        setupAuthentication(true);
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(mapService.getMapsByTripId(1L, 1L)).thenReturn(List.of(testMapData));

        // Act
        ResponseEntity<?> response = mapController.getMapsByTrip(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(List.class, response.getBody());
        verify(mapService, times(1)).getMapsByTripId(1L, 1L);
    }

    @Test
    void getMapsByTrip_WithoutAuthentication_ShouldReturnUnauthorized() {
        // Arrange
        setupAuthentication(false);

        // Act
        ResponseEntity<?> response = mapController.getMapsByTrip(1L);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Not authenticated", response.getBody());
        verify(mapService, never()).getMapsByTripId(anyLong(), anyLong());
    }

    @Test
    void getMapsByTrip_WithNullAuthentication_ShouldReturnUnauthorized() {
        // Arrange
        setupAuthenticationNull();

        // Act
        ResponseEntity<?> response = mapController.getMapsByTrip(1L);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Not authenticated", response.getBody());
        verify(mapService, never()).getMapsByTripId(anyLong(), anyLong());
    }

    @Test
    void getMapsByTrip_WithAnonymousUser_ShouldReturnUnauthorized() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        // Act
        ResponseEntity<?> response = mapController.getMapsByTrip(1L);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Not authenticated", response.getBody());
        verify(mapService, never()).getMapsByTripId(anyLong(), anyLong());
    }

    @Test
    void getMapsByTrip_WithUserNotFound_ShouldReturnNotFound() {
        // Arrange
        setupAuthentication(true);
        when(userService.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = mapController.getMapsByTrip(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
        verify(mapService, never()).getMapsByTripId(anyLong(), anyLong());
    }

    // ========== Tests for getMapById ==========

    @Test
    void getMapById_WithValidMap_ShouldReturnMap() {
        // Arrange
        setupAuthentication(true);
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(mapService.getMapById(1L)).thenReturn(Optional.of(testMapData));

        // Act
        ResponseEntity<?> response = mapController.getMapById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(MapResponse.class, response.getBody());
        verify(mapService, times(1)).getMapById(1L);
    }

    @Test
    void getMapById_WithoutAuthentication_ShouldReturnUnauthorized() {
        // Arrange
        setupAuthentication(false);

        // Act
        ResponseEntity<?> response = mapController.getMapById(1L);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Not authenticated", response.getBody());
        verify(mapService, never()).getMapById(anyLong());
    }

    @Test
    void getMapById_WithNullAuthentication_ShouldReturnUnauthorized() {
        // Arrange
        setupAuthenticationNull();

        // Act
        ResponseEntity<?> response = mapController.getMapById(1L);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Not authenticated", response.getBody());
        verify(mapService, never()).getMapById(anyLong());
    }

    @Test
    void getMapById_WithAnonymousUser_ShouldReturnUnauthorized() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        // Act
        ResponseEntity<?> response = mapController.getMapById(1L);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Not authenticated", response.getBody());
        verify(mapService, never()).getMapById(anyLong());
    }

    @Test
    void getMapById_WithUserNotFound_ShouldReturnNotFound() {
        // Arrange
        setupAuthentication(true);
        when(userService.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = mapController.getMapById(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
        verify(mapService, never()).getMapById(anyLong());
    }

    @Test
    void getMapById_WithMapNotFound_ShouldReturnNotFound() {
        // Arrange
        setupAuthentication(true);
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(mapService.getMapById(999L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = mapController.getMapById(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Map not found", response.getBody());
    }

    @Test
    void getMapById_WithUnauthorizedUser_ShouldReturnForbidden() {
        // Arrange
        setupAuthentication(true);
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        MapData otherUserMap = new MapData();
        otherUserMap.setId(2L);
        otherUserMap.setUser(otherUser);
        when(mapService.getMapById(2L)).thenReturn(Optional.of(otherUserMap));

        // Act
        ResponseEntity<?> response = mapController.getMapById(2L);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Not authorized to access this map", response.getBody());
    }

    // ========== Tests for getAllMapsForUser ==========

    @Test
    void getAllMapsForUser_WithAuthentication_ShouldReturnMaps() {
        // Arrange
        setupAuthentication(true);
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(mapService.getMapsForUser(1L)).thenReturn(List.of(testMapData));

        // Act
        ResponseEntity<?> response = mapController.getAllMapsForUser();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(List.class, response.getBody());
        verify(mapService, times(1)).getMapsForUser(1L);
    }

    @Test
    void getAllMapsForUser_WithoutAuthentication_ShouldReturnUnauthorized() {
        // Arrange
        setupAuthentication(false);

        // Act
        ResponseEntity<?> response = mapController.getAllMapsForUser();

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Not authenticated", response.getBody());
        verify(mapService, never()).getMapsForUser(anyLong());
    }

    @Test
    void getAllMapsForUser_WithNullAuthentication_ShouldReturnUnauthorized() {
        // Arrange
        setupAuthenticationNull();

        // Act
        ResponseEntity<?> response = mapController.getAllMapsForUser();

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Not authenticated", response.getBody());
        verify(mapService, never()).getMapsForUser(anyLong());
    }

    @Test
    void getAllMapsForUser_WithAnonymousUser_ShouldReturnUnauthorized() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        // Act
        ResponseEntity<?> response = mapController.getAllMapsForUser();

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Not authenticated", response.getBody());
        verify(mapService, never()).getMapsForUser(anyLong());
    }

    @Test
    void getAllMapsForUser_WithUserNotFound_ShouldReturnNotFound() {
        // Arrange
        setupAuthentication(true);
        when(userService.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = mapController.getAllMapsForUser();

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
        verify(mapService, never()).getMapsForUser(anyLong());
    }

    // ========== Tests for updateMap ==========

    @Test
    void updateMap_WithValidData_ShouldReturnUpdatedMap() {
        // Arrange
        setupAuthentication(true);
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(mapService.getMapById(1L)).thenReturn(Optional.of(testMapData));
        when(mapService.updateMap(eq(1L), any(CreateMapRequest.class))).thenReturn(testMapData);

        // Act
        ResponseEntity<?> response = mapController.updateMap(1L, createRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(MapResponse.class, response.getBody());
        verify(mapService, times(1)).updateMap(eq(1L), any(CreateMapRequest.class));
    }

    @Test
    void updateMap_WithoutAuthentication_ShouldReturnUnauthorized() {
        // Arrange
        setupAuthentication(false);

        // Act
        ResponseEntity<?> response = mapController.updateMap(1L, createRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Not authenticated", response.getBody());
        verify(mapService, never()).updateMap(anyLong(), any());
    }

    @Test
    void updateMap_WithNullAuthentication_ShouldReturnUnauthorized() {
        // Arrange
        setupAuthenticationNull();

        // Act
        ResponseEntity<?> response = mapController.updateMap(1L, createRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Not authenticated", response.getBody());
        verify(mapService, never()).updateMap(anyLong(), any());
    }

    @Test
    void updateMap_WithAnonymousUser_ShouldReturnUnauthorized() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        // Act
        ResponseEntity<?> response = mapController.updateMap(1L, createRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Not authenticated", response.getBody());
        verify(mapService, never()).updateMap(anyLong(), any());
    }

    @Test
    void updateMap_WithUserNotFound_ShouldReturnNotFound() {
        // Arrange
        setupAuthentication(true);
        when(userService.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = mapController.updateMap(1L, createRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
        verify(mapService, never()).updateMap(anyLong(), any());
    }

    @Test
    void updateMap_WithMapNotFound_ShouldReturnNotFound() {
        // Arrange
        setupAuthentication(true);
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(mapService.getMapById(999L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = mapController.updateMap(999L, createRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Map not found", response.getBody());
        verify(mapService, never()).updateMap(anyLong(), any());
    }

    @Test
    void updateMap_WithUnauthorizedUser_ShouldReturnForbidden() {
        // Arrange
        setupAuthentication(true);
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        MapData otherUserMap = new MapData();
        otherUserMap.setId(2L);
        otherUserMap.setUser(otherUser);
        when(mapService.getMapById(2L)).thenReturn(Optional.of(otherUserMap));

        // Act
        ResponseEntity<?> response = mapController.updateMap(2L, createRequest);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Not authorized to update this map", response.getBody());
        verify(mapService, never()).updateMap(anyLong(), any());
    }

    @Test
    void updateMap_WithIllegalArgumentException_ShouldReturnBadRequest() {
        // Arrange
        setupAuthentication(true);
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(mapService.getMapById(1L)).thenReturn(Optional.of(testMapData));
        when(mapService.updateMap(eq(1L), any(CreateMapRequest.class)))
            .thenThrow(new IllegalArgumentException("Invalid data"));

        // Act
        ResponseEntity<?> response = mapController.updateMap(1L, createRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid data", response.getBody());
    }

    @Test
    void updateMap_WithGenericException_ShouldReturnInternalServerError() {
        // Arrange
        setupAuthentication(true);
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(mapService.getMapById(1L)).thenReturn(Optional.of(testMapData));
        when(mapService.updateMap(eq(1L), any(CreateMapRequest.class)))
            .thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<?> response = mapController.updateMap(1L, createRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to update map", response.getBody());
    }

    // ========== Tests for deleteMap ==========

    @Test
    void deleteMap_WithValidMap_ShouldReturnSuccess() {
        // Arrange
        setupAuthentication(true);
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(mapService.getMapById(1L)).thenReturn(Optional.of(testMapData));
        doNothing().when(mapService).deleteMap(1L);

        // Act
        ResponseEntity<?> response = mapController.deleteMap(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Map deleted successfully", response.getBody());
        verify(mapService, times(1)).deleteMap(1L);
    }

    @Test
    void deleteMap_WithoutAuthentication_ShouldReturnUnauthorized() {
        // Arrange
        setupAuthentication(false);

        // Act
        ResponseEntity<?> response = mapController.deleteMap(1L);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Not authenticated", response.getBody());
        verify(mapService, never()).deleteMap(anyLong());
    }

    @Test
    void deleteMap_WithNullAuthentication_ShouldReturnUnauthorized() {
        // Arrange
        setupAuthenticationNull();

        // Act
        ResponseEntity<?> response = mapController.deleteMap(1L);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Not authenticated", response.getBody());
        verify(mapService, never()).deleteMap(anyLong());
    }

    @Test
    void deleteMap_WithAnonymousUser_ShouldReturnUnauthorized() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        // Act
        ResponseEntity<?> response = mapController.deleteMap(1L);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Not authenticated", response.getBody());
        verify(mapService, never()).deleteMap(anyLong());
    }

    @Test
    void deleteMap_WithUserNotFound_ShouldReturnNotFound() {
        // Arrange
        setupAuthentication(true);
        when(userService.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = mapController.deleteMap(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
        verify(mapService, never()).deleteMap(anyLong());
    }

    @Test
    void deleteMap_WithMapNotFound_ShouldReturnNotFound() {
        // Arrange
        setupAuthentication(true);
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(mapService.getMapById(999L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = mapController.deleteMap(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Map not found", response.getBody());
        verify(mapService, never()).deleteMap(anyLong());
    }

    @Test
    void deleteMap_WithUnauthorizedUser_ShouldReturnForbidden() {
        // Arrange
        setupAuthentication(true);
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        MapData otherUserMap = new MapData();
        otherUserMap.setId(2L);
        otherUserMap.setUser(otherUser);
        when(mapService.getMapById(2L)).thenReturn(Optional.of(otherUserMap));

        // Act
        ResponseEntity<?> response = mapController.deleteMap(2L);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Not authorized to delete this map", response.getBody());
        verify(mapService, never()).deleteMap(anyLong());
    }

    @Test
    void deleteMap_WithException_ShouldReturnInternalServerError() {
        // Arrange
        setupAuthentication(true);
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(mapService.getMapById(1L)).thenReturn(Optional.of(testMapData));
        doThrow(new RuntimeException("Database error")).when(mapService).deleteMap(1L);

        // Act
        ResponseEntity<?> response = mapController.deleteMap(1L);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to delete map", response.getBody());
    }
}

