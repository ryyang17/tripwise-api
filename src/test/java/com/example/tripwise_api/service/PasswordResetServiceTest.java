package com.example.tripwise_api.service;

import com.example.tripwise_api.model.PasswordResetToken;
import com.example.tripwise_api.model.User;
import com.example.tripwise_api.repository.PasswordResetTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {
    @Mock
    private PasswordResetTokenRepository tokenRepository;
    @Mock
    private UserService userService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private PasswordResetService passwordResetService;

    private User user;
    private PasswordResetToken token;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("oldPassword");
        token = new PasswordResetToken(UUID.randomUUID().toString(), user, LocalDateTime.now().plusHours(1));
    }

    @Test
    void createPasswordResetToken_WithValidUserAndEmail_ShouldReturnToken() {
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(tokenRepository.save(any(PasswordResetToken.class))).thenReturn(token);
        doNothing().when(tokenRepository).deleteByUser(user);

        String result = passwordResetService.createPasswordResetToken("testuser", "test@example.com");
        assertNotNull(result);
    }

    @Test
    void createPasswordResetToken_WithInvalidEmail_ShouldReturnNull() {
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        String result = passwordResetService.createPasswordResetToken("testuser", "wrong@example.com");
        assertNull(result);
    }

    @Test
    void createPasswordResetToken_WithNonExistentUser_ShouldReturnNull() {
        when(userService.findByUsername("nouser")).thenReturn(Optional.empty());
        String result = passwordResetService.createPasswordResetToken("nouser", "test@example.com");
        assertNull(result);
    }

    @Test
    void resetPassword_WithValidToken_ShouldSucceed() {
        token.setUsed(false);
        when(tokenRepository.findByToken(token.getToken())).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userService.saveUserDirectly(any(User.class))).thenReturn(user);
        when(tokenRepository.save(any(PasswordResetToken.class))).thenReturn(token);

        boolean result = passwordResetService.resetPassword(token.getToken(), "newPassword");
        assertTrue(result);
        assertTrue(token.isUsed());
    }

    @Test
    void resetPassword_WithExpiredToken_ShouldFail() {
        token.setUsed(false);
        token.setExpiryDate(LocalDateTime.now().minusMinutes(1));
        when(tokenRepository.findByToken(token.getToken())).thenReturn(Optional.of(token));

        boolean result = passwordResetService.resetPassword(token.getToken(), "newPassword");
        assertFalse(result);
    }

    @Test
    void resetPassword_WithUsedToken_ShouldFail() {
        token.setUsed(true);
        when(tokenRepository.findByToken(token.getToken())).thenReturn(Optional.of(token));

        boolean result = passwordResetService.resetPassword(token.getToken(), "newPassword");
        assertFalse(result);
    }

    @Test
    void resetPassword_WithInvalidToken_ShouldFail() {
        when(tokenRepository.findByToken("invalidToken")).thenReturn(Optional.empty());
        boolean result = passwordResetService.resetPassword("invalidToken", "newPassword");
        assertFalse(result);
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        token.setUsed(false);
        when(tokenRepository.findByToken(token.getToken())).thenReturn(Optional.of(token));
        assertTrue(passwordResetService.validateToken(token.getToken()));
    }

    @Test
    void validateToken_WithExpiredToken_ShouldReturnFalse() {
        token.setUsed(false);
        token.setExpiryDate(LocalDateTime.now().minusMinutes(1));
        when(tokenRepository.findByToken(token.getToken())).thenReturn(Optional.of(token));
        assertFalse(passwordResetService.validateToken(token.getToken()));
    }

    @Test
    void validateToken_WithUsedToken_ShouldReturnFalse() {
        token.setUsed(true);
        when(tokenRepository.findByToken(token.getToken())).thenReturn(Optional.of(token));
        assertFalse(passwordResetService.validateToken(token.getToken()));
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnFalse() {
        when(tokenRepository.findByToken("invalidToken")).thenReturn(Optional.empty());
        assertFalse(passwordResetService.validateToken("invalidToken"));
    }
}

