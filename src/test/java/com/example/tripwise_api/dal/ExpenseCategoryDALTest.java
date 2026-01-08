package com.example.tripwise_api.dal;

import com.example.tripwise_api.model.ExpenseCategory;
import com.example.tripwise_api.repository.ExpenseCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseCategoryDALTest {

    @Mock
    private ExpenseCategoryRepository expenseCategoryRepository;

    @InjectMocks
    private ExpenseCategoryDAL expenseCategoryDAL;

    private ExpenseCategory testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new ExpenseCategory();
        testCategory.setCategoryId(1L);
        testCategory.setCategoryName("Transportation");
        testCategory.setCategoryDescription("Transport related expenses");
        testCategory.setIsActive(true);
        testCategory.setCreatedDate(LocalDateTime.now());
    }

    @Test
    void getAllExpenseCategories_ShouldReturnAllCategories() {
        // Arrange
        ExpenseCategory category2 = new ExpenseCategory();
        category2.setCategoryId(2L);
        category2.setCategoryName("Food");
        List<ExpenseCategory> expectedCategories = List.of(testCategory, category2);
        when(expenseCategoryRepository.findAll()).thenReturn(expectedCategories);

        // Act
        List<ExpenseCategory> result = expenseCategoryDAL.getAllExpenseCategories();

        // Assert
        assertEquals(expectedCategories, result);
        assertEquals(2, result.size());
        verify(expenseCategoryRepository, times(1)).findAll();
    }

    @Test
    void getAllExpenseCategories_WhenEmpty_ShouldReturnEmptyList() {
        // Arrange
        when(expenseCategoryRepository.findAll()).thenReturn(List.of());

        // Act
        List<ExpenseCategory> result = expenseCategoryDAL.getAllExpenseCategories();

        // Assert
        assertTrue(result.isEmpty());
        verify(expenseCategoryRepository, times(1)).findAll();
    }

    @Test
    void getExpenseCategoryById_WhenExists_ShouldReturnCategory() {
        // Arrange
        when(expenseCategoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        // Act
        Optional<ExpenseCategory> result = expenseCategoryDAL.getExpenseCategoryById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testCategory, result.get());
        assertEquals("Transportation", result.get().getCategoryName());
        verify(expenseCategoryRepository, times(1)).findById(1L);
    }

    @Test
    void getExpenseCategoryById_WhenNotExists_ShouldReturnEmpty() {
        // Arrange
        when(expenseCategoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<ExpenseCategory> result = expenseCategoryDAL.getExpenseCategoryById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(expenseCategoryRepository, times(1)).findById(999L);
    }

    @Test
    void saveExpenseCategory_ShouldSaveAndReturnCategory() {
        // Arrange
        when(expenseCategoryRepository.save(any(ExpenseCategory.class))).thenReturn(testCategory);

        // Act
        ExpenseCategory result = expenseCategoryDAL.saveExpenseCategory(testCategory);

        // Assert
        assertNotNull(result);
        assertEquals(testCategory, result);
        assertEquals("Transportation", result.getCategoryName());
        verify(expenseCategoryRepository, times(1)).save(testCategory);
    }

    @Test
    void saveExpenseCategory_WithNewCategory_ShouldSaveSuccessfully() {
        // Arrange
        ExpenseCategory newCategory = new ExpenseCategory("Accommodation", "Hotel and lodging expenses");
        newCategory.setCategoryId(3L);
        when(expenseCategoryRepository.save(any(ExpenseCategory.class))).thenReturn(newCategory);

        // Act
        ExpenseCategory result = expenseCategoryDAL.saveExpenseCategory(newCategory);

        // Assert
        assertNotNull(result);
        assertEquals("Accommodation", result.getCategoryName());
        assertEquals("Hotel and lodging expenses", result.getCategoryDescription());
        verify(expenseCategoryRepository, times(1)).save(newCategory);
    }

    @Test
    void deleteExpenseCategory_ShouldDeleteCategory() {
        // Arrange
        doNothing().when(expenseCategoryRepository).deleteById(1L);

        // Act
        expenseCategoryDAL.deleteExpenseCategory(1L);

        // Assert
        verify(expenseCategoryRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteExpenseCategory_WithNonExistentId_ShouldStillInvokeDelete() {
        // Arrange
        doNothing().when(expenseCategoryRepository).deleteById(999L);

        // Act
        expenseCategoryDAL.deleteExpenseCategory(999L);

        // Assert
        verify(expenseCategoryRepository, times(1)).deleteById(999L);
    }

    @Test
    void existsById_WhenExists_ShouldReturnTrue() {
        // Arrange
        when(expenseCategoryRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = expenseCategoryDAL.existsById(1L);

        // Assert
        assertTrue(result);
        verify(expenseCategoryRepository, times(1)).existsById(1L);
    }

    @Test
    void existsById_WhenNotExists_ShouldReturnFalse() {
        // Arrange
        when(expenseCategoryRepository.existsById(999L)).thenReturn(false);

        // Act
        boolean result = expenseCategoryDAL.existsById(999L);

        // Assert
        assertFalse(result);
        verify(expenseCategoryRepository, times(1)).existsById(999L);
    }
}

