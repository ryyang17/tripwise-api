package com.example.tripwise_api.service;

import com.example.tripwise_api.model.PasswordResetToken;
import com.example.tripwise_api.model.User;
import com.example.tripwise_api.repository.PasswordResetTokenRepository;
import com.example.tripwise_api.service.exception.UserNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    // Token expiry time in hours
    private static final int TOKEN_EXPIRY_HOURS = 1;

    public PasswordResetService(
            PasswordResetTokenRepository tokenRepository,
            UserService userService,
            PasswordEncoder passwordEncoder) {
        this.tokenRepository = tokenRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Create a password reset token for a user (identified by username and email)
     * Returns the token string if successful, throws UserNotFoundException if user not found or email does not match
     */
    public String createPasswordResetToken(String username, String email) {
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty() || !userOpt.get().getEmail().equalsIgnoreCase(email)) {
            throw new UserNotFoundException("User and email not found");
        }
        User user = userOpt.get();

        // Delete any existing tokens for this user
        tokenRepository.deleteByUser(user);

        // Generate new token
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS);

        PasswordResetToken resetToken = new PasswordResetToken(token, user, expiryDate);
        tokenRepository.save(resetToken);

        return token;
    }

    /**
     * Reset password using token
     * Returns true if successful, false if token invalid/expired
     */
    public boolean resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> resetTokenOpt = tokenRepository.findByToken(token);

        if (resetTokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetToken resetToken = resetTokenOpt.get();

        // Check if token is valid
        if (!resetToken.isValid()) {
            return false;
        }

        // Update user password - encode once and save directly
        User user = resetToken.getUser();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        // Save user directly to avoid double encoding in updateUser method
        userService.saveUserDirectly(user);

        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        return true;
    }

    /**
     * Validate if a token exists and is valid
     */
    @Transactional(readOnly = true)
    public boolean validateToken(String token) {
        Optional<PasswordResetToken> resetTokenOpt = tokenRepository.findByToken(token);
        return resetTokenOpt.isPresent() && resetTokenOpt.get().isValid();
    }

    /**
     * Clean up expired tokens (can be called periodically)
     */
    public void cleanupExpiredTokens() {
        tokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }
}
