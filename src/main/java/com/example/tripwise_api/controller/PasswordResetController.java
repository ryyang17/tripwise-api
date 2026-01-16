package com.example.tripwise_api.controller;

import com.example.tripwise_api.dto.request.ResetPasswordRequest;
import com.example.tripwise_api.service.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(originPatterns = "*")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    // POST /api/auth/forgot-password - Request password reset with username and email
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@Valid @RequestBody Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");

        // Validate inputs
        if (username == null || username.isBlank()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Username is required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        if (email == null || email.isBlank()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Email is required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        try {
            String token = passwordResetService.createPasswordResetToken(username, email);

            if (token == null) {
                // Don't reveal whether user exists - security best practice
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "If the username and email combination is valid, a password reset token has been generated");
                return ResponseEntity.ok(response);
            }

            // Return token directly (simple implementation)
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Password reset token generated successfully");
            response.put("token", token);
            response.put("expiresIn", "1 hour");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "An error occurred while processing your request");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // POST /api/auth/reset-password - Reset password with token
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        // Validate that passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "New password and confirm password do not match");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        try {
            boolean success = passwordResetService.resetPassword(request.getToken(), request.getNewPassword());

            if (!success) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid or expired reset token");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Password has been reset successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "An error occurred while resetting your password");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // GET /api/auth/validate-reset-token - Validate if a reset token is valid
    @GetMapping("/validate-reset-token")
    public ResponseEntity<Map<String, Object>> validateResetToken(@RequestParam String token) {
        boolean isValid = passwordResetService.validateToken(token);

        Map<String, Object> response = new HashMap<>();
        response.put("valid", isValid);

        if (isValid) {
            response.put("message", "Token is valid");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Token is invalid or expired");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}

