package com.example.tripwise_api.Service;

import com.example.tripwise_api.Model.User;
import com.example.tripwise_api.DAL.UserDAL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserDAL userDAL;

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userDAL.getAllUsers();
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userDAL.getUserById(id);
    }

    public User createUser(User user) {
        user.setCreatedDate(LocalDateTime.now());
        return userDAL.saveUser(user);
    }

    public User updateUser(Long id, User userDetails) {
        User user = userDAL.getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(userDetails.getPassword());
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
}
