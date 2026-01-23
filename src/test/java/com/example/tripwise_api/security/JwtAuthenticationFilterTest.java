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
        // arrange
        when(request.getRequestURI()).thenReturn("/api/auth/login");

        // act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // assert
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_WithValidToken_ShouldAuthenticateUser() throws ServletException, IOException {
        // arrange
        when(request.getRequestURI()).thenReturn("/api/users/me");
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(jwtUtil.extractUsername("valid-token")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtil.validateAccessToken("valid-token", "testuser")).thenReturn(true);

        // act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // assert
        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("testuser", SecurityContextHolder.getContext().getAuthentication().getName());
    }


    @Test
    void doFilterInternal_WithoutAuthorizationHeader_ShouldNotAuthenticate() throws ServletException, IOException {
        // arrange
        when(request.getRequestURI()).thenReturn("/api/users/me");
        when(request.getHeader("Authorization")).thenReturn(null);
        HttpServletResponse localResponse = mock(HttpServletResponse.class, RETURNS_DEEP_STUBS);
        java.io.PrintWriter mockWriter = mock(java.io.PrintWriter.class);
        when(localResponse.getWriter()).thenReturn(mockWriter);

        // act
        jwtAuthenticationFilter.doFilterInternal(request, localResponse, filterChain);

        // assert
        verify(localResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(mockWriter, atLeastOnce()).write(anyString());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_WithExpiredToken_ShouldReturnUnauthorized() throws ServletException, IOException {
        // arramge
        when(request.getRequestURI()).thenReturn("/api/users/me");
        when(request.getHeader("Authorization")).thenReturn("Bearer expired-token");
        when(jwtUtil.extractUsername("expired-token")).thenThrow(new io.jsonwebtoken.ExpiredJwtException(null, null, "Token expired"));
        HttpServletResponse localResponse = mock(HttpServletResponse.class, RETURNS_DEEP_STUBS);
        java.io.PrintWriter mockWriter = mock(java.io.PrintWriter.class);
        when(localResponse.getWriter()).thenReturn(mockWriter);

        // act
        jwtAuthenticationFilter.doFilterInternal(request, localResponse, filterChain);

        // assert
        verify(localResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(mockWriter).write(contains("Access token expired"));
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }


    @Test
    void doFilterInternal_WithAlreadyAuthenticatedUser_ShouldContinueWithoutReauthentication() throws ServletException, IOException {
        // arrange
        when(request.getRequestURI()).thenReturn("/api/users/me");
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(jwtUtil.extractUsername("valid-token")).thenReturn("testuser");
        // Zet een bestaande authenticatie in de SecurityContext
        UsernamePasswordAuthenticationToken existingAuth =
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(existingAuth);
        // act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // assert
        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("testuser", SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
