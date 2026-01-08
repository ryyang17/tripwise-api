package com.example.tripwise_api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.request.ServletWebRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("GlobalErrorController Tests")
class GlobalErrorControllerTest {

    private MockMvc mockMvc;
    private ErrorAttributes errorAttributes;

    @BeforeEach
    void setUp() {
        errorAttributes = mock(ErrorAttributes.class);
        GlobalErrorController controller = new GlobalErrorController(errorAttributes);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("should return sanitized path and friendly message for 404 errors")
    void handleNotFoundErrorProvidesFriendlyResponse() throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("status", 404);
        attributes.put("error", "Not Found");
        attributes.put("message", "Resource not found");
        attributes.put("path", "/api/missing-resource?token=secret");
        attributes.put("timestamp", Instant.now());

        when(errorAttributes.getErrorAttributes(any(ServletWebRequest.class), any(ErrorAttributeOptions.class)))
            .thenReturn(attributes);

        mockMvc.perform(get("/error"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.message").value("The requested resource '/api/missing-resource' was not found. Please check the URL."))
            .andExpect(jsonPath("$.path").value("/api/missing-resource"))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("should return special message for 404 errors on auth endpoints")
    void handleNotFoundOnAuthEndpointProvidesSpecificMessage() throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("status", 404);
        attributes.put("error", "Not Found");
        attributes.put("message", "Resource not found");
        attributes.put("path", "/api/auth/invalid");
        attributes.put("timestamp", Instant.now());

        when(errorAttributes.getErrorAttributes(any(ServletWebRequest.class), any(ErrorAttributeOptions.class)))
            .thenReturn(attributes);

        mockMvc.perform(get("/error"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.message").value("Authentication endpoint not found. Please check the URL and use the correct HTTP method."))
            .andExpect(jsonPath("$.path").value("/api/auth/invalid"));
    }

    @Test
    @DisplayName("should return authentication message for 401 errors with bad credentials")
    void handleUnauthorizedErrorWithBadCredentials() throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("status", 401);
        attributes.put("error", "Unauthorized");
        attributes.put("message", "Bad credentials");
        attributes.put("path", "/api/login");
        attributes.put("timestamp", Instant.now());

        when(errorAttributes.getErrorAttributes(any(ServletWebRequest.class), any(ErrorAttributeOptions.class)))
            .thenReturn(attributes);

        mockMvc.perform(get("/error"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.message").value("Invalid username or password. Please check and try again."))
            .andExpect(jsonPath("$.path").value("/api/login"));
    }

    @Test
    @DisplayName("should return generic authentication message for 401 errors without bad credentials")
    void handleUnauthorizedErrorGeneric() throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("status", 401);
        attributes.put("error", "Unauthorized");
        attributes.put("message", "Full authentication is required");
        attributes.put("path", "/api/trips");
        attributes.put("timestamp", Instant.now());

        when(errorAttributes.getErrorAttributes(any(ServletWebRequest.class), any(ErrorAttributeOptions.class)))
            .thenReturn(attributes);

        mockMvc.perform(get("/error"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.message").value("Authentication required. Please log in with valid credentials."))
            .andExpect(jsonPath("$.path").value("/api/trips"));
    }

    @Test
    @DisplayName("should return access denied message for 403 errors")
    void handleForbiddenError() throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("status", 403);
        attributes.put("error", "Forbidden");
        attributes.put("message", "Access Denied");
        attributes.put("path", "/api/admin/users");
        attributes.put("timestamp", Instant.now());

        when(errorAttributes.getErrorAttributes(any(ServletWebRequest.class), any(ErrorAttributeOptions.class)))
            .thenReturn(attributes);

        mockMvc.perform(get("/error"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.status").value(403))
            .andExpect(jsonPath("$.message").value("Access denied. Please make sure you're logged in and have permission to access this resource."))
            .andExpect(jsonPath("$.path").value("/api/admin/users"));
    }

    @Test
    @DisplayName("should return validation message for 400 errors with validation keyword")
    void handleBadRequestWithValidation() throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("status", 400);
        attributes.put("error", "Bad Request");
        attributes.put("message", "Validation failed for object");
        attributes.put("path", "/api/trips");
        attributes.put("timestamp", Instant.now());

        when(errorAttributes.getErrorAttributes(any(ServletWebRequest.class), any(ErrorAttributeOptions.class)))
            .thenReturn(attributes);

        mockMvc.perform(get("/error"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Invalid input data. Please check your request and try again."))
            .andExpect(jsonPath("$.path").value("/api/trips"));
    }

    @Test
    @DisplayName("should return generic message for 400 errors without validation keyword")
    void handleBadRequestGeneric() throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("status", 400);
        attributes.put("error", "Bad Request");
        attributes.put("message", "Malformed JSON");
        attributes.put("path", "/api/trips");
        attributes.put("timestamp", Instant.now());

        when(errorAttributes.getErrorAttributes(any(ServletWebRequest.class), any(ErrorAttributeOptions.class)))
            .thenReturn(attributes);

        mockMvc.perform(get("/error"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Bad request. Please check your input and try again."))
            .andExpect(jsonPath("$.path").value("/api/trips"));
    }

    @Test
    @DisplayName("should return internal server error message for 500 errors")
    void handleInternalServerError() throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("status", 500);
        attributes.put("error", "Internal Server Error");
        attributes.put("message", "Database connection failed");
        attributes.put("path", "/api/trips");
        attributes.put("timestamp", Instant.now());

        when(errorAttributes.getErrorAttributes(any(ServletWebRequest.class), any(ErrorAttributeOptions.class)))
            .thenReturn(attributes);

        mockMvc.perform(get("/error"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.message").value("An internal server error occurred. Please try again later or contact support."))
            .andExpect(jsonPath("$.path").value("/api/trips"));
    }

    @Test
    @DisplayName("should return original message for unhandled status codes")
    void handleUnhandledStatusCode() throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("status", 503);
        attributes.put("error", "Service Unavailable");
        attributes.put("message", "Service temporarily unavailable");
        attributes.put("path", "/api/trips");
        attributes.put("timestamp", Instant.now());

        when(errorAttributes.getErrorAttributes(any(ServletWebRequest.class), any(ErrorAttributeOptions.class)))
            .thenReturn(attributes);

        mockMvc.perform(get("/error"))
            .andExpect(status().isServiceUnavailable())
            .andExpect(jsonPath("$.status").value(503))
            .andExpect(jsonPath("$.message").value("Service temporarily unavailable"))
            .andExpect(jsonPath("$.path").value("/api/trips"));
    }

    @Test
    @DisplayName("should return default message when original message is empty")
    void handleUnhandledStatusCodeWithEmptyMessage() throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("status", 503);
        attributes.put("error", "Service Unavailable");
        attributes.put("message", "");
        attributes.put("path", "/api/trips");
        attributes.put("timestamp", Instant.now());

        when(errorAttributes.getErrorAttributes(any(ServletWebRequest.class), any(ErrorAttributeOptions.class)))
            .thenReturn(attributes);

        mockMvc.perform(get("/error"))
            .andExpect(status().isServiceUnavailable())
            .andExpect(jsonPath("$.status").value(503))
            .andExpect(jsonPath("$.message").value("An unexpected error occurred. Please try again."))
            .andExpect(jsonPath("$.path").value("/api/trips"));
    }

    @Test
    @DisplayName("should sanitize path by removing query parameters")
    void pathSanitizationRemovesQueryParameters() throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("status", 404);
        attributes.put("error", "Not Found");
        attributes.put("message", "Resource not found");
        attributes.put("path", "/api/trips?userId=123&sessionId=xyz");
        attributes.put("timestamp", Instant.now());

        when(errorAttributes.getErrorAttributes(any(ServletWebRequest.class), any(ErrorAttributeOptions.class)))
            .thenReturn(attributes);

        mockMvc.perform(get("/error"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.path").value("/api/trips"))
            .andExpect(jsonPath("$.message").value("The requested resource '/api/trips' was not found. Please check the URL."));
    }

    @Test
    @DisplayName("should handle null path gracefully")
    void handleNullPath() throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("status", 500);
        attributes.put("error", "Internal Server Error");
        attributes.put("message", "An error occurred");
        attributes.put("path", null);
        attributes.put("timestamp", Instant.now());

        when(errorAttributes.getErrorAttributes(any(ServletWebRequest.class), any(ErrorAttributeOptions.class)))
            .thenReturn(attributes);

        mockMvc.perform(get("/error"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.path").value("/"));
    }

    @Test
    @DisplayName("should handle status as Number type")
    void handleStatusAsNumber() throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("status", 404L); // Long instead of Integer
        attributes.put("error", "Not Found");
        attributes.put("message", "Resource not found");
        attributes.put("path", "/api/trips");
        attributes.put("timestamp", Instant.now());

        when(errorAttributes.getErrorAttributes(any(ServletWebRequest.class), any(ErrorAttributeOptions.class)))
            .thenReturn(attributes);

        mockMvc.perform(get("/error"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("should default to 500 when status is missing")
    void handleMissingStatus() throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("error", "Unknown Error");
        attributes.put("message", "Something went wrong");
        attributes.put("path", "/api/trips");
        attributes.put("timestamp", Instant.now());

        when(errorAttributes.getErrorAttributes(any(ServletWebRequest.class), any(ErrorAttributeOptions.class)))
            .thenReturn(attributes);

        mockMvc.perform(get("/error"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.status").value(500));
    }
}

