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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Value("${cookie.secure}")
    private boolean cookieSecure;

    @Value("${cookie.same-site}")
    private String cookieSameSite;

    @Value("${cookie.domain}")
    private String cookieDomain;

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
            logger.error("Login failed: {}", e.getMessage(), e);
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
            return ResponseEntity.status(HttpStatus.CONFLICT).body(authResponse);
        } catch (Exception e) {
            logger.error("Registration failed: {}", e.getMessage(), e);
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
            logger.error("Token refresh failed: {}", e.getMessage(), e);
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
            logger.error("Logout failed: {}", e.getMessage(), e);
            AuthResponse authResponse = new AuthResponse(null, null, "Logout failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(authResponse);
        }
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        // roept helper met max age van 30 dagen aan
        int thirtyDaysInSeconds = 30 * 24 * 60 * 60;
        writeRefreshCookie(response, refreshToken, thirtyDaysInSeconds);
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        // roept helper met max age van 0 seconden aan
        writeRefreshCookie(response, "", 0);
    }


    private void writeRefreshCookie(HttpServletResponse response, String value, int maxAgeSeconds) {
        Cookie servletCookie = new Cookie("refreshToken", value);
        servletCookie.setHttpOnly(true);
        servletCookie.setSecure(cookieSecure);
        servletCookie.setPath("/");
        servletCookie.setMaxAge(maxAgeSeconds);

        if (cookieDomain != null && !cookieDomain.trim().isEmpty()) {
            servletCookie.setDomain(cookieDomain);
        }
        response.addCookie(servletCookie);

        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from("refreshToken", value)
            .httpOnly(true)
            .secure(cookieSecure)
            .path("/")
            .maxAge(maxAgeSeconds);

        if (cookieDomain != null && !cookieDomain.trim().isEmpty()) {
            builder = builder.domain(cookieDomain);
        }

        if (cookieSameSite != null && !cookieSameSite.trim().isEmpty()) {
            builder = builder.sameSite(cookieSameSite);
        }

        response.addHeader("Set-Cookie", builder.build().toString());
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
