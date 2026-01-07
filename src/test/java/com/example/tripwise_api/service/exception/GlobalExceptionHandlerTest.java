package com.example.tripwise_api.service.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        // Setup is done via @InjectMocks
    }

    @Test
    void handleIllegalArgumentException_ShouldReturnBadRequestWithMessage() {
        // Arrange
        String errorMessage = "Invalid argument provided";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        // Act
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleIllegalArgumentException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Bad Request", response.getBody().get("error"));
        assertEquals(errorMessage, response.getBody().get("message"));
    }

    @Test
    void handleIllegalArgumentException_WithNullMessage_ShouldReturnBadRequest() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException();

        // Act
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleIllegalArgumentException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Bad Request", response.getBody().get("error"));
    }

    @Test
    void handleIllegalArgumentException_WithEmptyMessage_ShouldReturnBadRequest() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("");

        // Act
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleIllegalArgumentException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Bad Request", response.getBody().get("error"));
        assertEquals("", response.getBody().get("message"));
    }

    @Test
    void handleValidationException_ShouldReturnBadRequestWithFieldErrors() {
        // Arrange
        FieldError fieldError1 = new FieldError("user", "username", "Username is required");
        FieldError fieldError2 = new FieldError("user", "email", "Email must be valid");

        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleValidationException(methodArgumentNotValidException);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation Failed", response.getBody().get("error"));

        @SuppressWarnings("unchecked")
        List<Map<String, String>> fieldErrors = (List<Map<String, String>>) response.getBody().get("fieldErrors");
        assertNotNull(fieldErrors);
        assertEquals(2, fieldErrors.size());

        // Verify first field error
        assertEquals("username", fieldErrors.get(0).get("field"));
        assertEquals("Username is required", fieldErrors.get(0).get("message"));

        // Verify second field error
        assertEquals("email", fieldErrors.get(1).get("field"));
        assertEquals("Email must be valid", fieldErrors.get(1).get("message"));
    }

    @Test
    void handleValidationException_WithSingleFieldError_ShouldReturnBadRequest() {
        // Arrange
        FieldError fieldError = new FieldError("trip", "name", "Trip name cannot be blank");

        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleValidationException(methodArgumentNotValidException);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        List<Map<String, String>> fieldErrors = (List<Map<String, String>>) response.getBody().get("fieldErrors");
        assertNotNull(fieldErrors);
        assertEquals(1, fieldErrors.size());
        assertEquals("name", fieldErrors.get(0).get("field"));
        assertEquals("Trip name cannot be blank", fieldErrors.get(0).get("message"));
    }

    @Test
    void handleValidationException_WithNoFieldErrors_ShouldReturnEmptyList() {
        // Arrange
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleValidationException(methodArgumentNotValidException);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation Failed", response.getBody().get("error"));

        @SuppressWarnings("unchecked")
        List<Map<String, String>> fieldErrors = (List<Map<String, String>>) response.getBody().get("fieldErrors");
        assertNotNull(fieldErrors);
        assertTrue(fieldErrors.isEmpty());
    }

    @Test
    void handleRuntimeException_ShouldReturnInternalServerError() {
        // Arrange
        String errorMessage = "Unexpected runtime error occurred";
        RuntimeException exception = new RuntimeException(errorMessage);

        // Act
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleRuntimeException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal Server Error", response.getBody().get("error"));
        assertEquals(errorMessage, response.getBody().get("message"));
    }

    @Test
    void handleRuntimeException_WithNullMessage_ShouldReturnInternalServerError() {
        // Arrange
        RuntimeException exception = new RuntimeException();

        // Act
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleRuntimeException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal Server Error", response.getBody().get("error"));
    }

    @Test
    void handleRuntimeException_WithNestedCause_ShouldReturnInternalServerError() {
        // Arrange
        String errorMessage = "Database connection failed";
        RuntimeException exception = new RuntimeException(errorMessage, new Exception("Connection timeout"));

        // Act
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleRuntimeException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal Server Error", response.getBody().get("error"));
        assertEquals(errorMessage, response.getBody().get("message"));
    }

    @Test
    void handleRuntimeException_WithSpecificRuntimeException_ShouldReturnInternalServerError() {
        // Arrange
        String errorMessage = "Null pointer encountered";
        NullPointerException exception = new NullPointerException(errorMessage);

        // Act
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleRuntimeException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal Server Error", response.getBody().get("error"));
        assertEquals(errorMessage, response.getBody().get("message"));
    }

    @Test
    void handleValidationException_WithMultipleErrorsOnSameField_ShouldReturnAllErrors() {
        // Arrange
        FieldError fieldError1 = new FieldError("expense", "amount", "Amount is required");
        FieldError fieldError2 = new FieldError("expense", "amount", "Amount must be positive");
        FieldError fieldError3 = new FieldError("expense", "description", "Description is required");

        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2, fieldError3));

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleValidationException(methodArgumentNotValidException);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        List<Map<String, String>> fieldErrors = (List<Map<String, String>>) response.getBody().get("fieldErrors");
        assertNotNull(fieldErrors);
        assertEquals(3, fieldErrors.size());

        // Verify amount field has two errors
        long amountErrors = fieldErrors.stream()
                .filter(error -> "amount".equals(error.get("field")))
                .count();
        assertEquals(2, amountErrors);

        // Verify description field has one error
        long descriptionErrors = fieldErrors.stream()
                .filter(error -> "description".equals(error.get("field")))
                .count();
        assertEquals(1, descriptionErrors);
    }
}

