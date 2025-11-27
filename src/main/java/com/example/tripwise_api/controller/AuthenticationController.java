package com.example.tripwise_api.controller;

import com.example.tripwise_api.dto.request.AuthRequest;
import com.example.tripwise_api.dto.request.RegisterRequest;
import com.example.tripwise_api.dto.response.AuthResponse;
import com.example.tripwise_api.model.User;
import com.example.tripwise_api.service.UserService;
import com.example.tripwise_api.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthenticationController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthenticationController(UserService userService, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    authRequest.getUsername(),
                    authRequest.getPassword()
                )
            );

            String token = jwtUtil.generateToken(authRequest.getUsername());
            AuthResponse response = new AuthResponse(token, authRequest.getUsername(), "Login successful");
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            AuthResponse response = new AuthResponse(null, null, "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            AuthResponse response = new AuthResponse(null, null, "Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User user = userService.registerUser(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                registerRequest.getPassword()
            );

            String token = jwtUtil.generateToken(user.getUsername());
            AuthResponse response = new AuthResponse(token, user.getUsername(), "Registration successful");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            AuthResponse response = new AuthResponse(null, null, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            AuthResponse response = new AuthResponse(null, null, "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<AuthResponse> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String username = jwtUtil.extractUsername(token);

                if (jwtUtil.validateToken(token, username)) {
                    AuthResponse response = new AuthResponse(token, username, "Token is valid");
                    return ResponseEntity.ok(response);
                } else {
                    AuthResponse response = new AuthResponse(null, null, "Token is invalid");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                }
            } else {
                AuthResponse response = new AuthResponse(null, null, "Authorization header is missing or invalid");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            AuthResponse response = new AuthResponse(null, null, "Token validation failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}
