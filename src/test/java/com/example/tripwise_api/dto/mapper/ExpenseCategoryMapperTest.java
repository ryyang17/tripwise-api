package com.example.tripwise_api.dto.mapper;

import com.example.tripwise_api.dto.request.CreateCategoryRequest;
import com.example.tripwise_api.dto.response.ExpenseCategoryResponse;
import com.example.tripwise_api.model.ExpenseCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseCategoryMapperTest {

    private CreateCategoryRequest createRequest;
    private ExpenseCategory category;

    @BeforeEach
    void setUp() {
        createRequest = new CreateCategoryRequest();
        createRequest.setCategoryName("Transportation");
        createRequest.setCategoryDescription("All transport related expenses");
        createRequest.setIsActive(true);

        category = new ExpenseCategory();
        category.setCategoryId(1L);
        category.setCategoryName("Transportation");
        category.setCategoryDescription("All transport related expenses");
        category.setIsActive(true);
        category.setCreatedDate(LocalDateTime.now());
    }

    @Test
    void toEntity_ShouldMapRequestToEntity() {
        // Act
        ExpenseCategory result = ExpenseCategoryMapper.toEntity(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Transportation", result.getCategoryName());
        assertEquals("All transport related expenses", result.getCategoryDescription());
        assertTrue(result.getIsActive());
        assertNotNull(result.getCreatedDate());
    }

    @Test
    void toEntity_WithNullRequest_ShouldReturnNull() {
        // Act
        ExpenseCategory result = ExpenseCategoryMapper.toEntity(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toEntity_WithMinimalData_ShouldSetDefaults() {
        // Arrange
        CreateCategoryRequest minimalRequest = new CreateCategoryRequest();
        minimalRequest.setCategoryName("Food");

        // Act
        ExpenseCategory result = ExpenseCategoryMapper.toEntity(minimalRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Food", result.getCategoryName());
        assertTrue(result.getIsActive()); // Default value
        assertNotNull(result.getCreatedDate()); // Auto-set
    }

    @Test
    void toResponse_ShouldMapEntityToResponse() {
        // Act
        ExpenseCategoryResponse result = ExpenseCategoryMapper.toResponse(category);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getCategoryId());
        assertEquals("Transportation", result.getCategoryName());
        assertEquals("All transport related expenses", result.getCategoryDescription());
        assertTrue(result.getIsActive());
        assertNotNull(result.getCreatedDate());
    }

    @Test
    void toResponse_WithNullEntity_ShouldReturnNull() {
        // Act
        ExpenseCategoryResponse result = ExpenseCategoryMapper.toResponse(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toResponse_WithInactiveCategory_ShouldMapCorrectly() {
        // Arrange
        category.setIsActive(false);

        // Act
        ExpenseCategoryResponse result = ExpenseCategoryMapper.toResponse(category);

        // Assert
        assertNotNull(result);
        assertFalse(result.getIsActive());
    }

    @Test
    void updateEntity_ShouldUpdateExistingEntity() {
        // Arrange
        ExpenseCategory existingCategory = new ExpenseCategory();
        existingCategory.setCategoryId(1L);
        existingCategory.setCategoryName("Old Name");
        existingCategory.setCategoryDescription("Old Description");
        LocalDateTime originalCreatedDate = LocalDateTime.now().minusDays(1);
        existingCategory.setCreatedDate(originalCreatedDate);

        CreateCategoryRequest updateRequest = new CreateCategoryRequest();
        updateRequest.setCategoryName("Updated Name");
        updateRequest.setCategoryDescription("Updated Description");

        // Act
        ExpenseCategoryMapper.updateEntity(existingCategory, updateRequest);

        // Assert
        assertEquals("Updated Name", existingCategory.getCategoryName());
        assertEquals("Updated Description", existingCategory.getCategoryDescription());
        assertEquals(originalCreatedDate, existingCategory.getCreatedDate()); // Should not change
    }

    @Test
    void updateEntity_WithNullEntity_ShouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> ExpenseCategoryMapper.updateEntity(null, createRequest));
    }

    @Test
    void updateEntity_WithNullRequest_ShouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> ExpenseCategoryMapper.updateEntity(category, null));
    }

    @Test
    void updateEntity_WithPartialData_ShouldUpdateOnlyProvidedFields() {
        // Arrange
        ExpenseCategory existingCategory = new ExpenseCategory();
        existingCategory.setCategoryName("Original Name");
        existingCategory.setCategoryDescription("Original Description");

        CreateCategoryRequest partialUpdate = new CreateCategoryRequest();
        partialUpdate.setCategoryName("Updated Name");
        // Description is null

        // Act
        ExpenseCategoryMapper.updateEntity(existingCategory, partialUpdate);

        // Assert
        assertEquals("Updated Name", existingCategory.getCategoryName());
        assertEquals("Original Description", existingCategory.getCategoryDescription()); // Should remain unchanged
    }

    @Test
    void toEntity_WithNullDescription_ShouldHandleGracefully() {
        // Arrange
        CreateCategoryRequest requestWithNullDescription = new CreateCategoryRequest();
        requestWithNullDescription.setCategoryName("Test Category");
        requestWithNullDescription.setCategoryDescription(null);

        // Act
        ExpenseCategory result = ExpenseCategoryMapper.toEntity(requestWithNullDescription);

        // Assert
        assertNotNull(result);
        assertEquals("Test Category", result.getCategoryName());
        assertNull(result.getCategoryDescription());
    }
}

