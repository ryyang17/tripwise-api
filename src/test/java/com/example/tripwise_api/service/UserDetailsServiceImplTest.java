package com.example.tripwise_api.service;

import com.example.tripwise_api.dal.UserDAL;
import com.example.tripwise_api.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserDAL userDAL;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword123");
        testUser.setEmail("test@example.com");
    }

    @Test
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        // Arrange
        String username = "testuser";
        when(userDAL.findByUsername(username)).thenReturn(Optional.of(testUser));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername(username);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("encodedPassword123", result.getPassword());
        assertTrue(result.getAuthorities().isEmpty());
        verify(userDAL, times(1)).findByUsername(username);
    }

    @Test
    void loadUserByUsername_WhenUserNotFound_ShouldThrowUsernameNotFoundException() {
        // Arrange
        String username = "nonexistent";
        when(userDAL.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
            UsernameNotFoundException.class,
            () -> userDetailsService.loadUserByUsername(username)
        );

        assertEquals("User not found: nonexistent", exception.getMessage());
        verify(userDAL, times(1)).findByUsername(username);
    }

    @Test
    void loadUserByUsername_WithDifferentUsername_ShouldReturnCorrectUserDetails() {
        // Arrange
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("anotheruser");
        anotherUser.setPassword("anotherPassword456");

        when(userDAL.findByUsername("anotheruser")).thenReturn(Optional.of(anotherUser));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername("anotheruser");

        // Assert
        assertEquals("anotheruser", result.getUsername());
        assertEquals("anotherPassword456", result.getPassword());
        verify(userDAL, times(1)).findByUsername("anotheruser");
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetailsWithEmptyAuthorities() {
        // Arrange
        when(userDAL.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(result.getAuthorities());
        assertTrue(result.getAuthorities().isEmpty());
    }

    @Test
    void loadUserByUsername_WithNullUsername_ShouldCallDAL() {
        // Arrange
        when(userDAL.findByUsername(null)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
            UsernameNotFoundException.class,
            () -> userDetailsService.loadUserByUsername(null)
        );
        verify(userDAL, times(1)).findByUsername(null);
    }

    @Test
    void loadUserByUsername_WithEmptyUsername_ShouldCallDAL() {
        // Arrange
        when(userDAL.findByUsername("")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
            UsernameNotFoundException.class,
            () -> userDetailsService.loadUserByUsername("")
        );

        assertEquals("User not found: ", exception.getMessage());
        verify(userDAL, times(1)).findByUsername("");
    }

    @Test
    void loadUserByUsername_ShouldReturnSpringSecurityUserDetails() {
        // Arrange
        when(userDAL.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertInstanceOf(org.springframework.security.core.userdetails.User.class, result);
    }

    @Test
    void loadUserByUsername_MultipleCalls_ShouldCallDALEachTime() {
        // Arrange
        when(userDAL.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        userDetailsService.loadUserByUsername("testuser");
        userDetailsService.loadUserByUsername("testuser");
        userDetailsService.loadUserByUsername("testuser");

        // Assert
        verify(userDAL, times(3)).findByUsername("testuser");
    }
}

