package com.example.tripwise_api.controller;

import com.example.tripwise_api.dto.request.ResetPasswordRequest;
import com.example.tripwise_api.service.PasswordResetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class PasswordResetControllerTest {
    @Mock
    private PasswordResetService passwordResetService;
    @InjectMocks
    private PasswordResetController controller;

    private Map<String, String> validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new HashMap<>();
        validRequest.put("username", "testuser");
        validRequest.put("email", "test@example.com");
    }

    @Test
    void forgotPassword_WithValidInput_ShouldReturnToken() {
        when(passwordResetService.createPasswordResetToken("testuser", "test@example.com")).thenReturn("token123");
        ResponseEntity<Map<String, Object>> response = controller.forgotPassword(validRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue((Boolean) response.getBody().get("success"));
        assertEquals("token123", response.getBody().get("token"));
    }

    @Test
    void forgotPassword_WithMissingUsername_ShouldReturnBadRequest() {
        validRequest.remove("username");
        ResponseEntity<Map<String, Object>> response = controller.forgotPassword(validRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username is required", response.getBody().get("error"));
    }

    @Test
    void forgotPassword_WithMissingEmail_ShouldReturnBadRequest() {
        validRequest.remove("email");
        ResponseEntity<Map<String, Object>> response = controller.forgotPassword(validRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email is required", response.getBody().get("error"));
    }

    @Test
    void forgotPassword_WithInvalidUser_ShouldReturnSuccessNoToken() {
        when(passwordResetService.createPasswordResetToken("testuser", "test@example.com")).thenReturn(null);
        ResponseEntity<Map<String, Object>> response = controller.forgotPassword(validRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue((Boolean) response.getBody().get("success"));
        assertNull(response.getBody().get("token"));
    }

    @Test
    void resetPassword_WithMatchingPasswords_ShouldSucceed() {
        ResetPasswordRequest req = new ResetPasswordRequest("token123", "newPassword", "newPassword");
        when(passwordResetService.resetPassword("token123", "newPassword")).thenReturn(true);
        ResponseEntity<Map<String, Object>> response = controller.resetPassword(req);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue((Boolean) response.getBody().get("success"));
    }

    @Test
    void resetPassword_WithNonMatchingPasswords_ShouldReturnBadRequest() {
        ResetPasswordRequest req = new ResetPasswordRequest("token123", "newPassword", "otherPassword");
        ResponseEntity<Map<String, Object>> response = controller.resetPassword(req);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("New password and confirm password do not match", response.getBody().get("error"));
    }

    @Test
    void resetPassword_WithInvalidToken_ShouldReturnBadRequest() {
        ResetPasswordRequest req = new ResetPasswordRequest("token123", "newPassword", "newPassword");
        when(passwordResetService.resetPassword("token123", "newPassword")).thenReturn(false);
        ResponseEntity<Map<String, Object>> response = controller.resetPassword(req);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid or expired reset token", response.getBody().get("error"));
    }

    @Test
    void validateResetToken_WithValidToken_ShouldReturnValidTrue() {
        when(passwordResetService.validateToken("token123")).thenReturn(true);
        ResponseEntity<Map<String, Object>> response = controller.validateResetToken("token123");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue((Boolean) response.getBody().get("valid"));
    }

    @Test
    void validateResetToken_WithInvalidToken_ShouldReturnValidFalse() {
        when(passwordResetService.validateToken("token123")).thenReturn(false);
        ResponseEntity<Map<String, Object>> response = controller.validateResetToken("token123");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse((Boolean) response.getBody().get("valid"));
    }
}

