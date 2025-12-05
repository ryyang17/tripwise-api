package com.example.tripwise_api.controller;

import com.example.tripwise_api.dto.request.AuthRequest;
import com.example.tripwise_api.dto.request.RegisterRequest;
import com.example.tripwise_api.dto.response.AuthResponse;
import com.example.tripwise_api.model.User;
import com.example.tripwise_api.service.UserService;
import com.example.tripwise_api.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
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
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest, HttpServletResponse response) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    authRequest.getUsername(),
                    authRequest.getPassword()
                )
            );

            String accessToken = jwtUtil.generateAccessToken(authRequest.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(authRequest.getUsername());

            // Set refresh token as HttpOnly cookie
            setRefreshTokenCookie(response, refreshToken);

            AuthResponse authResponse = new AuthResponse(accessToken, authRequest.getUsername(), "Login successful");
            return ResponseEntity.ok(authResponse);

        } catch (BadCredentialsException e) {
            AuthResponse authResponse = new AuthResponse(null, null, "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authResponse);
        } catch (Exception e) {
            AuthResponse authResponse = new AuthResponse(null, null, "Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(authResponse);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest, HttpServletResponse response) {
        try {
            User user = userService.registerUser(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                registerRequest.getPassword()
            );

            String accessToken = jwtUtil.generateAccessToken(user.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

            // Set refresh token as HttpOnly cookie
            setRefreshTokenCookie(response, refreshToken);

            AuthResponse authResponse = new AuthResponse(accessToken, user.getUsername(), "Registration successful");
            return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);

        } catch (IllegalArgumentException e) {
            AuthResponse authResponse = new AuthResponse(null, null, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(authResponse);
        } catch (Exception e) {
            AuthResponse authResponse = new AuthResponse(null, null, "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(authResponse);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        try {
            String refreshToken = getRefreshTokenFromCookie(request);

            if (refreshToken == null) {
                AuthResponse authResponse = new AuthResponse(null, null, "Refresh token not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authResponse);
            }

            String username = jwtUtil.extractUsername(refreshToken);

            if (username != null && jwtUtil.validateRefreshToken(refreshToken, username)) {
                String newAccessToken = jwtUtil.generateAccessToken(username);
                String newRefreshToken = jwtUtil.generateRefreshToken(username);

                // Update refresh token cookie
                setRefreshTokenCookie(response, newRefreshToken);

                AuthResponse authResponse = new AuthResponse(newAccessToken, username, "Token refreshed successfully");
                return ResponseEntity.ok(authResponse);
            } else {
                AuthResponse authResponse = new AuthResponse(null, null, "Invalid refresh token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authResponse);
            }

        } catch (Exception e) {
            AuthResponse authResponse = new AuthResponse(null, null, "Token refresh failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpServletResponse response) {
        try {
            // Clear refresh token cookie
            clearRefreshTokenCookie(response);

            AuthResponse authResponse = new AuthResponse(null, null, "Logout successful");
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            AuthResponse authResponse = new AuthResponse(null, null, "Logout failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(authResponse);
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<AuthResponse> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String username = jwtUtil.extractUsername(token);

                if (jwtUtil.validateAccessToken(token, username)) {
                    AuthResponse authResponse = new AuthResponse(token, username, "Token is valid");
                    return ResponseEntity.ok(authResponse);
                } else {
                    AuthResponse authResponse = new AuthResponse(null, null, "Token is invalid");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authResponse);
                }
            } else {
                AuthResponse authResponse = new AuthResponse(null, null, "Authorization header is missing or invalid");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(authResponse);
            }
        } catch (Exception e) {
            AuthResponse authResponse = new AuthResponse(null, null, "Token validation failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authResponse);
        }
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true in production with HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(30 * 24 * 60 * 60); // 30 days in seconds
        response.addCookie(cookie);
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true in production with HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(0); // Expire immediately
        response.addCookie(cookie);
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
