package com.example.tripwise_api.util;

import com.example.tripwise_api.model.User;
import com.example.tripwise_api.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class ControllerUtils {
    public static Optional<String> getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }
        return Optional.of(authentication.getName());
    }

    public static Optional<User> getUserIfAuthenticated(UserService userService) {
        Optional<String> usernameOpt = getAuthenticatedUsername();
        if (usernameOpt.isEmpty()) return Optional.empty();
        return userService.findByUsername(usernameOpt.get());
    }

    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal());
    }

    public static ResponseEntity<String> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
    }

    public static ResponseEntity<String> notFound(String entity) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(entity + " not found");
    }

    public static ResponseEntity<String> forbidden(String message) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
    }
}
