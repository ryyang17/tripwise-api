package com.example.tripwise_api.controller;

import com.example.tripwise_api.model.User;
import com.example.tripwise_api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setBio("Test bio");
        testUser.setLocation("Test City");
        testUser.setFavoriteDestination("Paris");
        testUser.setTravelStyle("Luxury");
        testUser.setNotifications(true);
        testUser.setCurrency("EUR");
        testUser.setCreatedDate(LocalDateTime.now());
    }

    @Test
    void getAllUsers_ShouldReturnUserList() {
        when(userService.getAllUsers()).thenReturn(List.of(testUser));
        ResponseEntity<List<Map<String, Object>>> response = userController.getAllUsers();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("testuser", response.getBody().get(0).get("username"));
    }


    @Test
    void getUserById_WhenExists_ShouldReturnUser() {
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));
        ResponseEntity<User> response = userController.getUserById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser, response.getBody());
    }

    @Test
    void getUserById_WhenNotExists_ShouldReturnNotFound() {
        when(userService.getUserById(2L)).thenReturn(Optional.empty());
        ResponseEntity<User> response = userController.getUserById(2L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void createUser_WithValidData_ShouldReturnCreated() {
        when(userService.createUser(any(User.class))).thenReturn(testUser);
        ResponseEntity<User> response = userController.createUser(testUser);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testUser, response.getBody());
    }

    @Test
    void createUser_WithInvalidData_ShouldReturnBadRequest() {
        when(userService.createUser(any(User.class))).thenThrow(new IllegalArgumentException());
        ResponseEntity<User> response = userController.createUser(testUser);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateUser_WhenExists_ShouldReturnUpdatedUser() {
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(testUser);
        ResponseEntity<User> response = userController.updateUser(1L, testUser);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser, response.getBody());
    }

    @Test
    void updateUser_WhenNotExists_ShouldReturnNotFound() {
        when(userService.updateUser(eq(2L), any(User.class))).thenThrow(new IllegalArgumentException());
        ResponseEntity<User> response = userController.updateUser(2L, testUser);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateUserCurrency_WhenExists_ShouldReturnOk() {
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(testUser);
        Map<String, String> request = new HashMap<>();
        request.put("currency", "USD");
        ResponseEntity<Map<String, Object>> response = userController.updateUserCurrency(1L, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("USD", response.getBody().get("currency"));
    }

    @Test
    void updateUserCurrency_WhenNotExists_ShouldReturnNotFound() {
        when(userService.getUserById(2L)).thenReturn(Optional.empty());
        Map<String, String> request = new HashMap<>();
        request.put("currency", "USD");
        ResponseEntity<Map<String, Object>> response = userController.updateUserCurrency(2L, request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateUserProfile_WhenExists_ShouldReturnOk() {
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(testUser);
        Map<String, String> updates = new HashMap<>();
        updates.put("bio", "New bio");
        updates.put("location", "New City");
        ResponseEntity<Map<String, Object>> response = userController.updateUserProfile(1L, updates);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue((Boolean) response.getBody().get("success"));
    }

    @Test
    void updateUserProfile_WhenNotExists_ShouldReturnNotFound() {
        when(userService.getUserById(2L)).thenReturn(Optional.empty());
        Map<String, String> updates = new HashMap<>();
        updates.put("bio", "New bio");
        ResponseEntity<Map<String, Object>> response = userController.updateUserProfile(2L, updates);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteUser_WhenExists_ShouldReturnOk() {
        doNothing().when(userService).deleteUser(1L);
        ResponseEntity<Map<String, Object>> response = userController.deleteUser(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue((Boolean) response.getBody().get("success"));
    }

    @Test
    void deleteUser_WhenNotExists_ShouldReturnNotFound() {
        doThrow(new IllegalArgumentException("User not found")).when(userService).deleteUser(2L);
        ResponseEntity<Map<String, Object>> response = userController.deleteUser(2L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
