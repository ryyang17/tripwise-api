package com.example.tripwise_api.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private static final String TEST_SECRET = "myVeryLongSecretKeyThatIsLongEnoughForHS512AlgorithmToWorkProperlyAndSecurelyWithAtLeast512BitsOfEntropy";
    private static final String TEST_USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "accessTokenExpiration", 900000L); // 15 minutes
        ReflectionTestUtils.setField(jwtUtil, "refreshTokenExpiration", 2592000000L); // 30 days
    }

    @Test
    void generateAccessToken_ShouldGenerateValidToken() {
        // Act
        String token = jwtUtil.generateAccessToken(TEST_USERNAME);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    void generateRefreshToken_ShouldGenerateValidToken() {
        // Act
        String token = jwtUtil.generateRefreshToken(TEST_USERNAME);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void extractUsername_WithValidToken_ShouldReturnUsername() {
        // Arrange
        String token = jwtUtil.generateAccessToken(TEST_USERNAME);

        // Act
        String username = jwtUtil.extractUsername(token);

        // Assert
        assertEquals(TEST_USERNAME, username);
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        // Arrange
        String token = jwtUtil.generateAccessToken(TEST_USERNAME);

        // Act
        boolean isValid = jwtUtil.validateToken(token, TEST_USERNAME);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_WithWrongUsername_ShouldReturnFalse() {
        // Arrange
        String token = jwtUtil.generateAccessToken(TEST_USERNAME);

        // Act
        boolean isValid = jwtUtil.validateToken(token, "wronguser");

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_WithExpiredToken_ShouldReturnFalse() {
        // Arrange
        ReflectionTestUtils.setField(jwtUtil, "accessTokenExpiration", -1000L);
        String token = jwtUtil.generateAccessToken(TEST_USERNAME);

        // Reset to normal expiration
        ReflectionTestUtils.setField(jwtUtil, "accessTokenExpiration", 900000L);

        // Act
        boolean isValid = jwtUtil.validateToken(token, TEST_USERNAME);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void extractUsername_WithInvalidToken_ShouldThrowException() {
        // Act & Assert
        assertThrows(MalformedJwtException.class, () -> {
            jwtUtil.extractUsername("invalid.token.here");
        });
    }

    @Test
    void validateAccessToken_WithValidAccessToken_ShouldReturnTrue() {
        // Arrange
        String token = jwtUtil.generateAccessToken(TEST_USERNAME);

        // Act
        boolean isValid = jwtUtil.validateAccessToken(token, TEST_USERNAME);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateRefreshToken_WithValidRefreshToken_ShouldReturnTrue() {
        // Arrange
        String token = jwtUtil.generateRefreshToken(TEST_USERNAME);

        // Act
        boolean isValid = jwtUtil.validateRefreshToken(token, TEST_USERNAME);

        // Assert
        assertTrue(isValid);
    }


    @Test
    void accessToken_ShouldHaveCorrectType() {
        // Arrange
        String token = jwtUtil.generateAccessToken(TEST_USERNAME);

        // Act
        String type = jwtUtil.extractClaim(token, claims -> claims.get("type", String.class));

        // Assert
        assertEquals("access", type);
    }

    @Test
    void refreshToken_ShouldHaveCorrectType() {
        // Arrange
        String token = jwtUtil.generateRefreshToken(TEST_USERNAME);

        // Act
        String type = jwtUtil.extractClaim(token, claims -> claims.get("type", String.class));

        // Assert
        assertEquals("refresh", type);
    }
}

