package com.example.tripwise_api.util;

import com.example.tripwise_api.model.User;
import com.example.tripwise_api.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ControllerUtilsTest {
    private SecurityContext originalContext;

    @BeforeEach
    void setUp() {
        // Save the original context to restore after test
        originalContext = SecurityContextHolder.getContext();
    }

    @AfterEach
    void tearDown() {
        // Restore the original context and clear context for isolation
        SecurityContextHolder.setContext(originalContext);
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAuthenticatedUsername_ShouldReturnUsername_WhenAuthenticated() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn("testuser");
        when(auth.getName()).thenReturn("testuser");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        Optional<String> username = ControllerUtils.getAuthenticatedUsername();
        assertTrue(username.isPresent());
        assertEquals("testuser", username.get());
    }

    @Test
    void getAuthenticatedUsername_ShouldReturnEmpty_WhenNotAuthenticated() {
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(context);

        Optional<String> username = ControllerUtils.getAuthenticatedUsername();
        assertTrue(username.isEmpty());
    }

    @Test
    void getAuthenticatedUsername_ShouldReturnEmpty_WhenAnonymousUser() {
        Authentication auth = new UsernamePasswordAuthenticationToken("anonymousUser", "password");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        Optional<String> username = ControllerUtils.getAuthenticatedUsername();
        assertTrue(username.isEmpty());
    }

    @Test
    void isAuthenticated_ShouldReturnTrue_WhenAuthenticated() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn("testuser");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        assertTrue(ControllerUtils.isAuthenticated());
    }

    @Test
    void isAuthenticated_ShouldReturnFalse_WhenNotAuthenticated() {
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(context);

        assertFalse(ControllerUtils.isAuthenticated());
    }

    @Test
    void isAuthenticated_ShouldReturnFalse_WhenAnonymousUser() {
        Authentication auth = new UsernamePasswordAuthenticationToken("anonymousUser", "password");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        assertFalse(ControllerUtils.isAuthenticated());
    }

    @Test
    void unauthorized_ShouldReturn401Response() {
        ResponseEntity<String> response = ControllerUtils.unauthorized();
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Not authenticated", response.getBody());
    }

    @Test
    void notFound_ShouldReturn404Response() {
        ResponseEntity<String> response = ControllerUtils.notFound("Trip");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Trip not found", response.getBody());
    }

    @Test
    void forbidden_ShouldReturn403Response() {
        ResponseEntity<String> response = ControllerUtils.forbidden("Forbidden message");
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Forbidden message", response.getBody());
    }

    @Test
    void getUserIfAuthenticated_ShouldReturnUser_WhenAuthenticated() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn("testuser");
        when(auth.getName()).thenReturn("testuser");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        UserService userService = mock(UserService.class);
        User user = new User();
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));

        Optional<User> result = ControllerUtils.getUserIfAuthenticated(userService);
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void getUserIfAuthenticated_ShouldReturnEmpty_WhenNotAuthenticated() {
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(context);

        UserService userService = mock(UserService.class);
        Optional<User> result = ControllerUtils.getUserIfAuthenticated(userService);
        assertTrue(result.isEmpty());
    }
}
