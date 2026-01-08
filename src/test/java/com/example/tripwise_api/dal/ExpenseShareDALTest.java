package com.example.tripwise_api.dal;

import com.example.tripwise_api.model.ExpenseShare;
import com.example.tripwise_api.model.User;
import com.example.tripwise_api.repository.ExpenseShareRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseShareDALTest {

    @Mock
    private ExpenseShareRepository expenseShareRepository;

    @InjectMocks
    private ExpenseShareDAL expenseShareDAL;

    private ExpenseShare testShare;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        testShare = new ExpenseShare();
        testShare.setId(1);
        testShare.setExpenseId(100);
        testShare.setUser(testUser);
        testShare.setAmount(new BigDecimal("25.50"));
        testShare.setIsSettled(false);
    }

    @Test
    void saveShare_ShouldSaveAndReturnShare() {
        // Arrange
        when(expenseShareRepository.save(any(ExpenseShare.class))).thenReturn(testShare);

        // Act
        ExpenseShare result = expenseShareDAL.saveShare(testShare);

        // Assert
        assertNotNull(result);
        assertEquals(testShare, result);
        assertEquals(new BigDecimal("25.50"), result.getAmount());
        verify(expenseShareRepository, times(1)).save(testShare);
    }

    @Test
    void saveShare_WithNewShare_ShouldSaveSuccessfully() {
        // Arrange
        ExpenseShare newShare = new ExpenseShare(200, 2L, new BigDecimal("50.00"));
        newShare.setId(2);
        when(expenseShareRepository.save(any(ExpenseShare.class))).thenReturn(newShare);

        // Act
        ExpenseShare result = expenseShareDAL.saveShare(newShare);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getId());
        assertEquals(200, result.getExpenseId());
        assertEquals(new BigDecimal("50.00"), result.getAmount());
        verify(expenseShareRepository, times(1)).save(newShare);
    }

    @Test
    void getSharesByExpenseId_ShouldReturnShares() {
        // Arrange
        ExpenseShare share2 = new ExpenseShare(100, 2L, new BigDecimal("30.00"));
        List<ExpenseShare> expectedShares = List.of(testShare, share2);
        when(expenseShareRepository.findByExpenseId(100)).thenReturn(expectedShares);

        // Act
        List<ExpenseShare> result = expenseShareDAL.getSharesByExpenseId(100);

        // Assert
        assertEquals(expectedShares, result);
        assertEquals(2, result.size());
        verify(expenseShareRepository, times(1)).findByExpenseId(100);
    }

    @Test
    void getSharesByExpenseId_WhenNoShares_ShouldReturnEmptyList() {
        // Arrange
        when(expenseShareRepository.findByExpenseId(999)).thenReturn(List.of());

        // Act
        List<ExpenseShare> result = expenseShareDAL.getSharesByExpenseId(999);

        // Assert
        assertTrue(result.isEmpty());
        verify(expenseShareRepository, times(1)).findByExpenseId(999);
    }

    @Test
    void getSharesByExpenseIdWithUserDetails_ShouldReturnSharesWithUsers() {
        // Arrange
        List<ExpenseShare> expectedShares = List.of(testShare);
        when(expenseShareRepository.findByExpenseIdWithUser(100)).thenReturn(expectedShares);

        // Act
        List<ExpenseShare> result = expenseShareDAL.getSharesByExpenseIdWithUserDetails(100);

        // Assert
        assertEquals(expectedShares, result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0).getUser());
        verify(expenseShareRepository, times(1)).findByExpenseIdWithUser(100);
    }

    @Test
    void getSharesByExpenseIdWithUserDetails_WhenNoShares_ShouldReturnEmptyList() {
        // Arrange
        when(expenseShareRepository.findByExpenseIdWithUser(999)).thenReturn(List.of());

        // Act
        List<ExpenseShare> result = expenseShareDAL.getSharesByExpenseIdWithUserDetails(999);

        // Assert
        assertTrue(result.isEmpty());
        verify(expenseShareRepository, times(1)).findByExpenseIdWithUser(999);
    }

    @Test
    void getSharesByUserId_ShouldReturnUserShares() {
        // Arrange
        ExpenseShare share2 = new ExpenseShare(200, 1L, new BigDecimal("15.00"));
        List<ExpenseShare> expectedShares = List.of(testShare, share2);
        when(expenseShareRepository.findByUserId(1L)).thenReturn(expectedShares);

        // Act
        List<ExpenseShare> result = expenseShareDAL.getSharesByUserId(1L);

        // Assert
        assertEquals(expectedShares, result);
        assertEquals(2, result.size());
        verify(expenseShareRepository, times(1)).findByUserId(1L);
    }

    @Test
    void getSharesByUserId_WhenNoShares_ShouldReturnEmptyList() {
        // Arrange
        when(expenseShareRepository.findByUserId(999L)).thenReturn(List.of());

        // Act
        List<ExpenseShare> result = expenseShareDAL.getSharesByUserId(999L);

        // Assert
        assertTrue(result.isEmpty());
        verify(expenseShareRepository, times(1)).findByUserId(999L);
    }

    @Test
    void getUnsettledSharesByUserId_ShouldReturnUnsettledShares() {
        // Arrange
        ExpenseShare unsettledShare = new ExpenseShare(100, 1L, new BigDecimal("20.00"));
        unsettledShare.setIsSettled(false);
        List<ExpenseShare> expectedShares = List.of(unsettledShare);
        when(expenseShareRepository.findUnsettledByUserId(1L)).thenReturn(expectedShares);

        // Act
        List<ExpenseShare> result = expenseShareDAL.getUnsettledSharesByUserId(1L);

        // Assert
        assertEquals(expectedShares, result);
        assertEquals(1, result.size());
        assertFalse(result.get(0).getIsSettled());
        verify(expenseShareRepository, times(1)).findUnsettledByUserId(1L);
    }

    @Test
    void getUnsettledSharesByUserId_WhenAllSettled_ShouldReturnEmptyList() {
        // Arrange
        when(expenseShareRepository.findUnsettledByUserId(1L)).thenReturn(List.of());

        // Act
        List<ExpenseShare> result = expenseShareDAL.getUnsettledSharesByUserId(1L);

        // Assert
        assertTrue(result.isEmpty());
        verify(expenseShareRepository, times(1)).findUnsettledByUserId(1L);
    }

    @Test
    void getUnsettledSharesByUserId_WhenNoUser_ShouldReturnEmptyList() {
        // Arrange
        when(expenseShareRepository.findUnsettledByUserId(999L)).thenReturn(List.of());

        // Act
        List<ExpenseShare> result = expenseShareDAL.getUnsettledSharesByUserId(999L);

        // Assert
        assertTrue(result.isEmpty());
        verify(expenseShareRepository, times(1)).findUnsettledByUserId(999L);
    }

    @Test
    void deleteSharesByExpenseId_ShouldDeleteAllSharesForExpense() {
        // Arrange
        doNothing().when(expenseShareRepository).deleteByExpenseId(100);

        // Act
        expenseShareDAL.deleteSharesByExpenseId(100);

        // Assert
        verify(expenseShareRepository, times(1)).deleteByExpenseId(100);
    }

    @Test
    void deleteSharesByExpenseId_WithNonExistentExpense_ShouldStillInvokeDelete() {
        // Arrange
        doNothing().when(expenseShareRepository).deleteByExpenseId(999);

        // Act
        expenseShareDAL.deleteSharesByExpenseId(999);

        // Assert
        verify(expenseShareRepository, times(1)).deleteByExpenseId(999);
    }
}

