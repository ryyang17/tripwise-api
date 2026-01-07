package com.example.tripwise_api.service;

import com.example.tripwise_api.dal.UserDAL;
import com.example.tripwise_api.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDAL userDAL;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = new User();
        validUser.setId(1L); // Changed from setUserId to setId
        validUser.setUsername("testuser");
        validUser.setEmail("test@example.com");
        validUser.setPassword("password123");
        validUser.setBio("Test bio");
        validUser.setLocation("Amsterdam");
        validUser.setFavoriteDestination("Paris");
        validUser.setTravelStyle("Adventure");
        validUser.setNotifications(true);
        validUser.setCurrency("EUR");
        validUser.setCreatedDate(LocalDateTime.now());
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Arrange
        List<User> expectedUsers = List.of(validUser);
        when(userDAL.getAllUsers()).thenReturn(expectedUsers);

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertEquals(expectedUsers, result);
        verify(userDAL, times(1)).getAllUsers();
    }

    @Test
    void getAllUsers_WhenNoUsers_ShouldReturnEmptyList() {
        // Arrange
        when(userDAL.getAllUsers()).thenReturn(Collections.emptyList());

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertTrue(result.isEmpty());
        verify(userDAL, times(1)).getAllUsers();
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        // Arrange
        when(userDAL.getUserById(1L)).thenReturn(Optional.of(validUser));

        // Act
        Optional<User> result = userService.getUserById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(validUser, result.get());
        verify(userDAL, times(1)).getUserById(1L);
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(userDAL.getUserById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.getUserById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(userDAL, times(1)).getUserById(999L);
    }

    @Test
    void createUser_WithValidUser_ShouldCreateUserWithCreatedDate() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setPassword("newpassword");

        when(userDAL.saveUser(any(User.class))).thenReturn(validUser);

        // Act
        User result = userService.createUser(newUser);

        // Assert
        assertEquals(validUser, result);
        assertNotNull(newUser.getCreatedDate());
        verify(userDAL, times(1)).saveUser(newUser);
    }

    @Test
    void createUser_ShouldSetCreatedDateAutomatically() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setCreatedDate(null); // Explicitly set to null

        when(userDAL.saveUser(any(User.class))).thenReturn(newUser);

        // Act
        LocalDateTime beforeCreate = LocalDateTime.now().minusSeconds(1);
        User result = userService.createUser(newUser);
        LocalDateTime afterCreate = LocalDateTime.now().plusSeconds(1);

        // Assert
        assertNotNull(newUser.getCreatedDate());
        assertTrue(newUser.getCreatedDate().isAfter(beforeCreate));
        assertTrue(newUser.getCreatedDate().isBefore(afterCreate));
        verify(userDAL, times(1)).saveUser(newUser);
    }

    @Test
    void updateUser_WhenUserExists_ShouldUpdateUser() {
        // Arrange
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("oldusername");
        existingUser.setEmail("old@example.com");
        existingUser.setPassword("oldpassword");

        User updateDetails = new User();
        updateDetails.setUsername("newusername");
        updateDetails.setEmail("new@example.com");
        updateDetails.setPassword("newpassword");
        updateDetails.setBio("Updated bio");
        updateDetails.setLocation("Rotterdam");
        updateDetails.setFavoriteDestination("London");
        updateDetails.setTravelStyle("Luxury");
        updateDetails.setNotifications(false);
        updateDetails.setCurrency("USD");

        when(userDAL.getUserById(1L)).thenReturn(Optional.of(existingUser));
        when(userDAL.saveUser(any(User.class))).thenReturn(existingUser);
        when(passwordEncoder.encode("newpassword")).thenReturn("newpassword");

        // Act
        User result = userService.updateUser(1L, updateDetails);

        // Assert
        assertNotNull(result);
        verify(userDAL, times(1)).getUserById(1L);
        verify(userDAL, times(1)).saveUser(existingUser);

        assertEquals("newusername", existingUser.getUsername());
        assertEquals("new@example.com", existingUser.getEmail());
        assertEquals("newpassword", existingUser.getPassword());
        assertEquals("Updated bio", existingUser.getBio());
        assertEquals("Rotterdam", existingUser.getLocation());
        assertEquals("London", existingUser.getFavoriteDestination());
        assertEquals("Luxury", existingUser.getTravelStyle());
        assertFalse(existingUser.getNotifications());
        assertEquals("USD", existingUser.getCurrency());
    }

    @Test
    void updateUser_WithNullPassword_ShouldNotUpdatePassword() {
        // Arrange
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setPassword("originalpassword");

        User updateDetails = new User();
        updateDetails.setUsername("newusername");
        updateDetails.setPassword(null); // Null password

        when(userDAL.getUserById(1L)).thenReturn(Optional.of(existingUser));
        when(userDAL.saveUser(any(User.class))).thenReturn(existingUser);

        // Act
        User result = userService.updateUser(1L, updateDetails);

        // Assert
        assertNotNull(result);
        assertEquals("originalpassword", existingUser.getPassword()); // Password unchanged
        assertEquals("newusername", existingUser.getUsername());
        verify(userDAL, times(1)).getUserById(1L);
        verify(userDAL, times(1)).saveUser(existingUser);
    }

    @Test
    void updateUser_WithEmptyPassword_ShouldNotUpdatePassword() {
        // Arrange
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setPassword("originalpassword");

        User updateDetails = new User();
        updateDetails.setUsername("newusername");
        updateDetails.setPassword(""); // Empty password

        when(userDAL.getUserById(1L)).thenReturn(Optional.of(existingUser));
        when(userDAL.saveUser(any(User.class))).thenReturn(existingUser);

        // Act
        User result = userService.updateUser(1L, updateDetails);

        // Assert
        assertNotNull(result);
        assertEquals("originalpassword", existingUser.getPassword()); // Password unchanged
        assertEquals("newusername", existingUser.getUsername());
        verify(userDAL, times(1)).getUserById(1L);
        verify(userDAL, times(1)).saveUser(existingUser);
    }

    @Test
    void updateUser_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        when(userDAL.getUserById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.updateUser(999L, validUser)
        );
        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userDAL, times(1)).getUserById(999L);
        verify(userDAL, never()).saveUser(any(User.class));
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDeleteUser() {
        // Arrange
        when(userDAL.existsById(1L)).thenReturn(true);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userDAL, times(1)).existsById(1L);
        verify(userDAL, times(1)).deleteUser(1L);
    }

    @Test
    void deleteUser_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        when(userDAL.existsById(999L)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.deleteUser(999L)
        );
        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userDAL, times(1)).existsById(999L);
        verify(userDAL, never()).deleteUser(999L);
    }

    @Test
    void updateUser_WithPartialUpdate_ShouldUpdateOnlyProvidedFields() {
        // Arrange
        User existingUser = new User();
        existingUser.setId(1L); // Changed from setUserId to setId
        existingUser.setUsername("originaluser");
        existingUser.setEmail("original@example.com");
        existingUser.setBio("Original bio");
        existingUser.setLocation("Original location");

        User partialUpdate = new User();
        partialUpdate.setUsername("updateduser");
        partialUpdate.setLocation("Updated location");
        // Other fields not set (null)

        when(userDAL.getUserById(1L)).thenReturn(Optional.of(existingUser));
        when(userDAL.saveUser(any(User.class))).thenReturn(existingUser);

        // Act
        User result = userService.updateUser(1L, partialUpdate);

        // Assert
        assertNotNull(result);
        assertEquals("updateduser", existingUser.getUsername());
        assertEquals("Updated location", existingUser.getLocation());
        // These should be set to null as they weren't provided in the update
        assertNull(existingUser.getEmail());
        assertNull(existingUser.getBio());
        verify(userDAL, times(1)).getUserById(1L);
        verify(userDAL, times(1)).saveUser(existingUser);
    }

    @Test
    void findByUsername_WhenUserExists_ShouldReturnUser() {
        // Arrange
        when(userDAL.findByUsername("testuser")).thenReturn(Optional.of(validUser));

        // Act
        Optional<User> result = userService.findByUsername("testuser");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(validUser, result.get());
        verify(userDAL, times(1)).findByUsername("testuser");
    }

    @Test
    void findByUsername_WhenUserDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(userDAL.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.findByUsername("nonexistent");

        // Assert
        assertFalse(result.isPresent());
        verify(userDAL, times(1)).findByUsername("nonexistent");
    }

    @Test
    void findByEmail_WhenUserExists_ShouldReturnUser() {
        // Arrange
        when(userDAL.findByEmail("test@example.com")).thenReturn(Optional.of(validUser));

        // Act
        Optional<User> result = userService.findByEmail("test@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(validUser, result.get());
        verify(userDAL, times(1)).findByEmail("test@example.com");
    }

    @Test
    void findByEmail_WhenUserDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(userDAL.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.findByEmail("nonexistent@example.com");

        // Assert
        assertFalse(result.isPresent());
        verify(userDAL, times(1)).findByEmail("nonexistent@example.com");
    }

    @Test
    void registerUser_WithValidData_ShouldCreateUser() {
        // Arrange
        String username = "newuser";
        String email = "newuser@example.com";
        String password = "password123";

        when(userDAL.findByUsername(username)).thenReturn(Optional.empty());
        when(userDAL.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(userDAL.saveUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = userService.registerUser(username, email, password);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(email, result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
        assertNotNull(result.getCreatedDate());
        verify(userDAL, times(1)).findByUsername(username);
        verify(userDAL, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).encode(password);
        verify(userDAL, times(1)).saveUser(any(User.class));
    }

    @Test
    void registerUser_WithExistingUsername_ShouldThrowException() {
        // Arrange
        String username = "existinguser";
        String email = "new@example.com";
        String password = "password123";

        when(userDAL.findByUsername(username)).thenReturn(Optional.of(validUser));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.registerUser(username, email, password)
        );

        assertEquals("Username already exists", exception.getMessage());
        verify(userDAL, times(1)).findByUsername(username);
        verify(userDAL, never()).findByEmail(anyString());
        verify(userDAL, never()).saveUser(any(User.class));
    }

    @Test
    void registerUser_WithExistingEmail_ShouldThrowException() {
        // Arrange
        String username = "newuser";
        String email = "existing@example.com";
        String password = "password123";

        when(userDAL.findByUsername(username)).thenReturn(Optional.empty());
        when(userDAL.findByEmail(email)).thenReturn(Optional.of(validUser));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.registerUser(username, email, password)
        );

        assertEquals("Email already exists", exception.getMessage());
        verify(userDAL, times(1)).findByUsername(username);
        verify(userDAL, times(1)).findByEmail(email);
        verify(userDAL, never()).saveUser(any(User.class));
    }

    @Test
    void authenticateUser_WithCorrectCredentials_ShouldReturnTrue() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        validUser.setPassword("encodedPassword");

        when(userDAL.findByUsername(username)).thenReturn(Optional.of(validUser));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(true);

        // Act
        boolean result = userService.authenticateUser(username, password);

        // Assert
        assertTrue(result);
        verify(userDAL, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).matches(password, "encodedPassword");
    }

    @Test
    void authenticateUser_WithIncorrectPassword_ShouldReturnFalse() {
        // Arrange
        String username = "testuser";
        String password = "wrongpassword";
        validUser.setPassword("encodedPassword");

        when(userDAL.findByUsername(username)).thenReturn(Optional.of(validUser));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(false);

        // Act
        boolean result = userService.authenticateUser(username, password);

        // Assert
        assertFalse(result);
        verify(userDAL, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).matches(password, "encodedPassword");
    }

    @Test
    void authenticateUser_WithNonExistentUser_ShouldReturnFalse() {
        // Arrange
        String username = "nonexistent";
        String password = "password123";

        when(userDAL.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        boolean result = userService.authenticateUser(username, password);

        // Assert
        assertFalse(result);
        verify(userDAL, times(1)).findByUsername(username);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void createUser_ShouldEncodePassword() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("plainPassword");

        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userDAL.saveUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = userService.createUser(newUser);

        // Assert
        assertEquals("encodedPassword", result.getPassword());
        verify(passwordEncoder, times(1)).encode("plainPassword");
        verify(userDAL, times(1)).saveUser(newUser);
    }
}
