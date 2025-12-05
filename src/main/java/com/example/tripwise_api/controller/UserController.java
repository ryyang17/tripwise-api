package com.example.tripwise_api.controller;

import com.example.tripwise_api.model.User;
import com.example.tripwise_api.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(originPatterns = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET /api/users - Get all users (for dropdown, trip participants)
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<User> users = userService.getAllUsers();

        List<Map<String, Object>> userList = users.stream()
                .map(user -> {
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("id", user.getId());
                    userInfo.put("username", user.getUsername());
                    userInfo.put("email", user.getEmail());
                    return userInfo;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(userList);
    }

    // GET /api/users/me - Get current authenticated user
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        String username = authentication.getName();
        Optional<User> userOpt = userService.findByUsername(username);

        if (userOpt.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        User user = userOpt.get();
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("bio", user.getBio());
        response.put("location", user.getLocation());
        response.put("favoriteDestination", user.getFavoriteDestination());
        response.put("travelStyle", user.getTravelStyle());
        response.put("notifications", user.getNotifications());
        response.put("currency", user.getCurrency());
        response.put("createdDate", user.getCreatedDate());

        return ResponseEntity.ok(response);
    }


    // GET /api/users/{id} - Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/users - Create new user
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            User savedUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT /api/users/{id} - Update existing user
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // PATCH /api/users/{id}/currency - Update user's preferred currency only
    @PatchMapping("/{id}/currency")
    public ResponseEntity<Map<String, Object>> updateUserCurrency(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            Optional<User> userOpt = userService.getUserById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();
            user.setCurrency(request.get("currency"));
            User updatedUser = userService.updateUser(id, user);

            Map<String, Object> response = buildSuccessResponse("Currency preference updated successfully");
            response.put("currency", updatedUser.getCurrency());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // PATCH /api/users/{id}/profile - Update user profile (bio, location, etc.)
    @PatchMapping("/{id}/profile")
    public ResponseEntity<Map<String, Object>> updateUserProfile(@PathVariable Long id, @RequestBody Map<String, String> updates) {
        try {
            Optional<User> userOpt = userService.getUserById(id);
            if (userOpt.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            User user = userOpt.get();

            // Update only provided fields
            if (updates.containsKey("bio")) {
                user.setBio(updates.get("bio"));
            }
            if (updates.containsKey("location")) {
                user.setLocation(updates.get("location"));
            }
            if (updates.containsKey("favoriteDestination")) {
                user.setFavoriteDestination(updates.get("favoriteDestination"));
            }
            if (updates.containsKey("travelStyle")) {
                user.setTravelStyle(updates.get("travelStyle"));
            }
            if (updates.containsKey("notifications")) {
                user.setNotifications(Boolean.parseBoolean(updates.get("notifications")));
            }
            if (updates.containsKey("currency")) {
                user.setCurrency(updates.get("currency"));
            }

            User updatedUser = userService.updateUser(id, user);

            Map<String, Object> response = buildSuccessResponse("Profile updated successfully");
            response.put("user", createUserResponse(updatedUser));

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // DELETE /api/users/{id} - Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            Map<String, Object> response = buildSuccessResponse("User deleted successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    // Helper to build a standard success response map
    private Map<String, Object> buildSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        return response;
    }

    // Helper method to create user response without password
    private Map<String, Object> createUserResponse(User user) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", user.getId());
        userResponse.put("username", user.getUsername());
        userResponse.put("email", user.getEmail());
        userResponse.put("bio", user.getBio());
        userResponse.put("location", user.getLocation());
        userResponse.put("favoriteDestination", user.getFavoriteDestination());
        userResponse.put("travelStyle", user.getTravelStyle());
        userResponse.put("notifications", user.getNotifications());
        userResponse.put("currency", user.getCurrency());
        userResponse.put("createdDate", user.getCreatedDate());
        return userResponse;
    }
}