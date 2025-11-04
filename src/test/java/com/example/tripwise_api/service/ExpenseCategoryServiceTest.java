package com.example.tripwise_api.service;

import com.example.tripwise_api.model.ExpenseCategory;
import com.example.tripwise_api.repository.ExpenseCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseCategoryServiceTest {

    @Mock
    private ExpenseCategoryRepository categoryRepository;

    @InjectMocks
    private ExpenseCategoryService expenseCategoryService;

    private ExpenseCategory validCategory;

    @BeforeEach
    void setUp() {
        validCategory = new ExpenseCategory();
        validCategory.setCategoryId(1L);
        validCategory.setCategoryName("Food");
        validCategory.setCategoryDescription("Food and dining expenses");
        validCategory.setIsActive(true);
    }

    @Test
    void getAllCategories_ShouldReturnAllCategories() {
        // Arrange
        List<ExpenseCategory> expectedCategories = List.of(validCategory);
        when(categoryRepository.findAll()).thenReturn(expectedCategories);

        // Act
        List<ExpenseCategory> result = expenseCategoryService.getAllCategories();

        // Assert
        assertEquals(expectedCategories, result);
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void getAllCategories_WhenNoCategories_ShouldReturnEmptyList() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<ExpenseCategory> result = expenseCategoryService.getAllCategories();

        // Assert
        assertTrue(result.isEmpty());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void getActiveCategories_ShouldReturnOnlyActiveCategories() {
        // Arrange
        List<ExpenseCategory> activeCategories = List.of(validCategory);
        when(categoryRepository.findByIsActiveTrue()).thenReturn(activeCategories);

        // Act
        List<ExpenseCategory> result = expenseCategoryService.getActiveCategories();

        // Assert
        assertEquals(activeCategories, result);
        verify(categoryRepository, times(1)).findByIsActiveTrue();
    }

    @Test
    void getActiveCategories_WhenNoActiveCategories_ShouldReturnEmptyList() {
        // Arrange
        when(categoryRepository.findByIsActiveTrue()).thenReturn(Collections.emptyList());

        // Act
        List<ExpenseCategory> result = expenseCategoryService.getActiveCategories();

        // Assert
        assertTrue(result.isEmpty());
        verify(categoryRepository, times(1)).findByIsActiveTrue();
    }

    @Test
    void getCategoryById_WhenCategoryExists_ShouldReturnCategory() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(validCategory));

        // Act
        Optional<ExpenseCategory> result = expenseCategoryService.getCategoryById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(validCategory, result.get());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void getCategoryById_WhenCategoryDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<ExpenseCategory> result = expenseCategoryService.getCategoryById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(categoryRepository, times(1)).findById(999L);
    }

    @Test
    void createCategory_WithValidCategory_ShouldCreateCategory() {
        // Arrange
        when(categoryRepository.existsByCategoryName("Food")).thenReturn(false);
        when(categoryRepository.save(any(ExpenseCategory.class))).thenReturn(validCategory);

        // Act
        ExpenseCategory result = expenseCategoryService.createCategory(validCategory);

        // Assert
        assertEquals(validCategory, result);
        verify(categoryRepository, times(1)).existsByCategoryName("Food");
        verify(categoryRepository, times(1)).save(validCategory);
    }

    @Test
    void createCategory_WithDuplicateName_ShouldThrowException() {
        // Arrange
        when(categoryRepository.existsByCategoryName("Food")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseCategoryService.createCategory(validCategory)
        );
        assertEquals("Category with name 'Food' already exists", exception.getMessage());
        verify(categoryRepository, times(1)).existsByCategoryName("Food");
        verify(categoryRepository, never()).save(any(ExpenseCategory.class));
    }

    @Test
    void updateCategory_WhenCategoryExists_ShouldUpdateCategory() {
        // Arrange
        ExpenseCategory existingCategory = new ExpenseCategory();
        existingCategory.setCategoryId(1L);
        existingCategory.setCategoryName("Old Name");
        existingCategory.setCategoryDescription("Old Description");
        existingCategory.setIsActive(false);

        ExpenseCategory updateDetails = new ExpenseCategory();
        updateDetails.setCategoryName("New Name");
        updateDetails.setCategoryDescription("New Description");
        updateDetails.setIsActive(true);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(ExpenseCategory.class))).thenReturn(existingCategory);

        // Act
        ExpenseCategory result = expenseCategoryService.updateCategory(1L, updateDetails);

        // Assert
        assertNotNull(result);
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(existingCategory);
        assertEquals("New Name", existingCategory.getCategoryName());
        assertEquals("New Description", existingCategory.getCategoryDescription());
        assertTrue(existingCategory.getIsActive());
    }

    @Test
    void updateCategory_WhenCategoryDoesNotExist_ShouldThrowException() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseCategoryService.updateCategory(999L, validCategory)
        );
        assertEquals("Category not found with id: 999", exception.getMessage());
        verify(categoryRepository, times(1)).findById(999L);
        verify(categoryRepository, never()).save(any(ExpenseCategory.class));
    }

    @Test
    void deactivateCategory_WhenCategoryExists_ShouldDeactivateCategory() {
        // Arrange
        ExpenseCategory activeCategory = new ExpenseCategory();
        activeCategory.setCategoryId(1L);
        activeCategory.setIsActive(true);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(activeCategory));
        when(categoryRepository.save(any(ExpenseCategory.class))).thenReturn(activeCategory);

        // Act
        expenseCategoryService.deactivateCategory(1L);

        // Assert
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(activeCategory);
        assertFalse(activeCategory.getIsActive());
    }

    @Test
    void deactivateCategory_WhenCategoryDoesNotExist_ShouldThrowException() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseCategoryService.deactivateCategory(999L)
        );
        assertEquals("Category not found with id: 999", exception.getMessage());
        verify(categoryRepository, times(1)).findById(999L);
        verify(categoryRepository, never()).save(any(ExpenseCategory.class));
    }

    @Test
    void deleteCategory_WhenCategoryExists_ShouldDeleteCategory() {
        // Arrange
        when(categoryRepository.existsById(1L)).thenReturn(true);

        // Act
        expenseCategoryService.deleteCategory(1L);

        // Assert
        verify(categoryRepository, times(1)).existsById(1L);
        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteCategory_WhenCategoryDoesNotExist_ShouldThrowException() {
        // Arrange
        when(categoryRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseCategoryService.deleteCategory(999L)
        );
        assertEquals("Category not found with id: 999", exception.getMessage());
        verify(categoryRepository, times(1)).existsById(999L);
        verify(categoryRepository, never()).deleteById(999L);
    }
}
