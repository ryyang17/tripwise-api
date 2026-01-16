package com.example.tripwise_api.service;

import com.example.tripwise_api.model.User;
import com.example.tripwise_api.dal.UserDAL;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserDAL userDAL;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserDAL userDAL, PasswordEncoder passwordEncoder) {
        this.userDAL = userDAL;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userDAL.getAllUsers();
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userDAL.getUserById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userDAL.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userDAL.findByEmail(email);
    }

    public User createUser(User user) {
        user.setCreatedDate(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userDAL.saveUser(user);
    }

    public User registerUser(String username, String email, String password) {
        // Check if username already exists
        if (userDAL.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Check if email already exists
        if (userDAL.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User(username, email, password);
        user.setPassword(passwordEncoder.encode(password));
        user.setCreatedDate(LocalDateTime.now());
        return userDAL.saveUser(user);
    }

    public boolean authenticateUser(String username, String password) {
        Optional<User> userOpt = userDAL.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }

    public User updateUser(Long id, User userDetails) {
        User user = userDAL.getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        user.setBio(userDetails.getBio());
        user.setLocation(userDetails.getLocation());
        user.setFavoriteDestination(userDetails.getFavoriteDestination());
        user.setTravelStyle(userDetails.getTravelStyle());
        user.setNotifications(userDetails.getNotifications());
        user.setCurrency(userDetails.getCurrency());

        return userDAL.saveUser(user);
    }

    public void deleteUser(Long id) {
        if (!userDAL.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userDAL.deleteUser(id);
    }

    public boolean changePassword(String username, String currentPassword, String newPassword) {
        Optional<User> userOpt = userDAL.findByUsername(username);

        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = userOpt.get();

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false; // Current password is incorrect
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userDAL.saveUser(user);

        return true; // Password successfully changed
    }

    /**
     * Save user directly without any password encoding.
     * Use this when the password is already encoded (e.g., in password reset flow).
     */
    public User saveUserDirectly(User user) {
        return userDAL.saveUser(user);
    }
}
