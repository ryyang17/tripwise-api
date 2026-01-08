package com.example.tripwise_api.security;

import com.example.tripwise_api.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        userDetails = new User("testuser", "password", new ArrayList<>());
    }

    @Test
    void doFilterInternal_WithPublicEndpoint_ShouldSkipAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/auth/login");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtUtil, never()).extractUsername(anyString());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_WithValidToken_ShouldAuthenticateUser() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/users/me");
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(jwtUtil.extractUsername("valid-token")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtil.validateAccessToken("valid-token", "testuser")).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("testuser", SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void doFilterInternal_WithoutAuthorizationHeader_ShouldNotAuthenticate() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/users/me");
        when(request.getHeader("Authorization")).thenReturn(null);
        HttpServletResponse localResponse = mock(HttpServletResponse.class, RETURNS_DEEP_STUBS);
        java.io.PrintWriter mockWriter = mock(java.io.PrintWriter.class);
        when(localResponse.getWriter()).thenReturn(mockWriter);
        // Act
        jwtAuthenticationFilter.doFilterInternal(request, localResponse, filterChain);
        // Assert
        verify(filterChain, never()).doFilter(any(), any());
        verify(localResponse).setStatus(eq(HttpServletResponse.SC_UNAUTHORIZED));
        verify(mockWriter, atLeastOnce()).write(anyString());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_WithInvalidTokenFormat_ShouldNotAuthenticate() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/users/me");
        when(request.getHeader("Authorization")).thenReturn("InvalidFormat");
        HttpServletResponse localResponse = mock(HttpServletResponse.class, RETURNS_DEEP_STUBS);
        java.io.PrintWriter mockWriter = mock(java.io.PrintWriter.class);
        when(localResponse.getWriter()).thenReturn(mockWriter);
        // Act
        jwtAuthenticationFilter.doFilterInternal(request, localResponse, filterChain);
        // Assert
        verify(filterChain, never()).doFilter(any(), any());
        verify(localResponse).setStatus(eq(HttpServletResponse.SC_UNAUTHORIZED));
        verify(mockWriter, atLeastOnce()).write(anyString());
        verify(jwtUtil, never()).extractUsername(anyString());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_WithExpiredToken_ShouldReturnUnauthorized() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/users/me");
        when(request.getHeader("Authorization")).thenReturn("Bearer expired-token");
        when(jwtUtil.extractUsername("expired-token")).thenThrow(new io.jsonwebtoken.ExpiredJwtException(null, null, "Token expired"));
        HttpServletResponse localResponse = mock(HttpServletResponse.class, RETURNS_DEEP_STUBS);
        java.io.PrintWriter mockWriter = mock(java.io.PrintWriter.class);
        when(localResponse.getWriter()).thenReturn(mockWriter);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, localResponse, filterChain);

        // Assert
        verify(filterChain, never()).doFilter(any(), any());
        verify(localResponse).setStatus(eq(HttpServletResponse.SC_UNAUTHORIZED));
        verify(mockWriter).write(contains("Access token expired"));
        verify(mockWriter).flush();
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_WithMalformedToken_ShouldReturnUnauthorized() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/users/me");
        when(request.getHeader("Authorization")).thenReturn("Bearer malformed-token");
        when(jwtUtil.extractUsername("malformed-token")).thenThrow(new io.jsonwebtoken.MalformedJwtException("Malformed token"));
        HttpServletResponse localResponse = mock(HttpServletResponse.class, RETURNS_DEEP_STUBS);
        java.io.PrintWriter mockWriter = mock(java.io.PrintWriter.class);
        when(localResponse.getWriter()).thenReturn(mockWriter);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, localResponse, filterChain);

        // Assert
        verify(filterChain, never()).doFilter(any(), any());
        verify(localResponse).setStatus(eq(HttpServletResponse.SC_UNAUTHORIZED));
        verify(mockWriter).write(contains("Invalid token"));
        verify(mockWriter).flush();
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_WithInvalidSignature_ShouldReturnUnauthorized() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/users/me");
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-signature-token");
        when(jwtUtil.extractUsername("invalid-signature-token")).thenThrow(new io.jsonwebtoken.security.SignatureException("Invalid signature"));
        HttpServletResponse localResponse = mock(HttpServletResponse.class, RETURNS_DEEP_STUBS);
        java.io.PrintWriter mockWriter = mock(java.io.PrintWriter.class);
        when(localResponse.getWriter()).thenReturn(mockWriter);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, localResponse, filterChain);

        // Assert
        verify(filterChain, never()).doFilter(any(), any());
        verify(localResponse).setStatus(eq(HttpServletResponse.SC_UNAUTHORIZED));
        verify(mockWriter).write(contains("Invalid token"));
        verify(mockWriter).flush();
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_WithGenericTokenExtractionException_ShouldReturnUnauthorized() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/users/me");
        when(request.getHeader("Authorization")).thenReturn("Bearer problematic-token");
        when(jwtUtil.extractUsername("problematic-token")).thenThrow(new RuntimeException("Unexpected error"));
        HttpServletResponse localResponse = mock(HttpServletResponse.class, RETURNS_DEEP_STUBS);
        java.io.PrintWriter mockWriter = mock(java.io.PrintWriter.class);
        when(localResponse.getWriter()).thenReturn(mockWriter);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, localResponse, filterChain);

        // Assert
        verify(filterChain, never()).doFilter(any(), any());
        verify(localResponse).setStatus(eq(HttpServletResponse.SC_UNAUTHORIZED));
        verify(mockWriter).write(contains("Token extraction failed"));
        verify(mockWriter).flush();
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_WithInvalidAccessToken_ShouldReturnUnauthorized() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/users/me");
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-format-but-invalid-token");
        when(jwtUtil.extractUsername("valid-format-but-invalid-token")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtil.validateAccessToken("valid-format-but-invalid-token", "testuser")).thenReturn(false);
        HttpServletResponse localResponse = mock(HttpServletResponse.class, RETURNS_DEEP_STUBS);
        java.io.PrintWriter mockWriter = mock(java.io.PrintWriter.class);
        when(localResponse.getWriter()).thenReturn(mockWriter);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, localResponse, filterChain);

        // Assert
        verify(filterChain, never()).doFilter(any(), any());
        verify(localResponse).setStatus(eq(HttpServletResponse.SC_UNAUTHORIZED));
        verify(mockWriter).write(contains("Invalid access token"));
        verify(mockWriter).flush();
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_WithAuthenticationException_ShouldReturnUnauthorized() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/users/me");
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(jwtUtil.extractUsername("valid-token")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenThrow(new RuntimeException("User service error"));
        HttpServletResponse localResponse = mock(HttpServletResponse.class, RETURNS_DEEP_STUBS);
        java.io.PrintWriter mockWriter = mock(java.io.PrintWriter.class);
        when(localResponse.getWriter()).thenReturn(mockWriter);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, localResponse, filterChain);

        // Assert
        verify(filterChain, never()).doFilter(any(), any());
        verify(localResponse).setStatus(eq(HttpServletResponse.SC_UNAUTHORIZED));
        verify(mockWriter).write(contains("Authentication error"));
        verify(mockWriter).flush();
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_WithSwaggerEndpoint_ShouldSkipAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/swagger-ui/index.html");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtUtil, never()).extractUsername(anyString());
    }

    @Test
    void doFilterInternal_WithActuatorEndpoint_ShouldSkipAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/actuator/health");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtUtil, never()).extractUsername(anyString());
    }

    @Test
    void doFilterInternal_WithErrorEndpoint_ShouldSkipAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/error");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtUtil, never()).extractUsername(anyString());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_WithApiDocsEndpoint_ShouldSkipAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/v3/api-docs");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtUtil, never()).extractUsername(anyString());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_WithAlreadyAuthenticatedUser_ShouldContinueWithoutReauthentication() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/users/me");
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(jwtUtil.extractUsername("valid-token")).thenReturn("testuser");

        // Set up existing authentication
        UsernamePasswordAuthenticationToken existingAuth =
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtUtil, never()).validateAccessToken(anyString(), anyString());
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("testuser", SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
