package com.example.tripwise_api.repository;

import com.example.tripwise_api.model.PasswordResetToken;
import com.example.tripwise_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUserAndUsedFalseAndExpiryDateAfter(User user, LocalDateTime now);

    void deleteByExpiryDateBefore(LocalDateTime now);

    void deleteByUser(User user);
}

