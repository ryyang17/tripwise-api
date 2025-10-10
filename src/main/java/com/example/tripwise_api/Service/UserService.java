package com.example.tripwise_api.Service;

import com.example.tripwise_api.Model.User;
import com.example.tripwise_api.DAL.UserDAL;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserDAL userDAL;

    public UserService(UserDAL userDAL) {
        this.userDAL = userDAL;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userDAL.getAllUsers();
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userDAL.getUserById(id);
    }

    public User createUser(User user) {
        // Add business validation here if needed
        return userDAL.saveUser(user);
    }

    public User updateUser(Long id, User userDetails) {
        User existingUser = userDAL.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        // Update fields as needed
        // ...existing code...
        return userDAL.saveUser(existingUser);
    }

    public void deleteUser(Long id) {
        if (!userDAL.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userDAL.deleteUser(id);
    }
}
