package com.example.tripwise_api.service;

import com.example.tripwise_api.dal.ExpenseShareDAL;
import com.example.tripwise_api.dto.mapper.ExpenseShareMapper;
import com.example.tripwise_api.model.ExpenseShare;
import com.example.tripwise_api.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseShareServiceTest {

    @Mock
    private ExpenseShareDAL expenseShareDAL;

    @Mock
    private ExpenseShareMapper expenseShareMapper;

    @Mock
    private TripParticipantService participantService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ExpenseShareService expenseShareService;

    private User user1;
    private User user2;
    private User user3;
    private ExpenseShare share1;
    private ExpenseShare share2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        user3 = new User();
        user3.setId(3L);
        user3.setUsername("user3");

        share1 = new ExpenseShare(100, 1L, new BigDecimal("50.00"));
        share1.setId(1);
        share1.setUser(user1);
        share1.setIsSettled(false);

        share2 = new ExpenseShare(100, 2L, new BigDecimal("50.00"));
        share2.setId(2);
        share2.setUser(user2);
        share2.setIsSettled(false);
    }

    @Test
    void createExpenseShares_WithValidUsers_ShouldCreateEqualShares() {
        // Arrange
        Integer expenseId = 100;
        Integer tripId = 1;
        List<Long> userIds = Arrays.asList(1L, 2L, 3L);
        BigDecimal totalAmount = new BigDecimal("150.00");
        BigDecimal expectedShareAmount = new BigDecimal("50.00");

        doNothing().when(participantService).validateUsersAreParticipants(tripId, userIds);
        when(userService.getUserById(1L)).thenReturn(Optional.of(user1));
        when(userService.getUserById(2L)).thenReturn(Optional.of(user2));
        when(userService.getUserById(3L)).thenReturn(Optional.of(user3));

        ExpenseShare savedShare1 = new ExpenseShare(expenseId, 1L, expectedShareAmount);
        ExpenseShare savedShare2 = new ExpenseShare(expenseId, 2L, expectedShareAmount);
        ExpenseShare savedShare3 = new ExpenseShare(expenseId, 3L, expectedShareAmount);

        when(expenseShareDAL.saveShare(any(ExpenseShare.class)))
            .thenReturn(savedShare1, savedShare2, savedShare3);

        // Act
        List<ExpenseShare> result = expenseShareService.createExpenseShares(expenseId, tripId, userIds, totalAmount);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(participantService, times(1)).validateUsersAreParticipants(tripId, userIds);
        verify(userService, times(3)).getUserById(anyLong());
        verify(expenseShareDAL, times(3)).saveShare(any(ExpenseShare.class));
    }

    @Test
    void createExpenseShares_WithUnevenDivision_ShouldRoundToTwoDecimals() {
        // Arrange
        Integer expenseId = 100;
        Integer tripId = 1;
        List<Long> userIds = Arrays.asList(1L, 2L, 3L);
        BigDecimal totalAmount = new BigDecimal("100.00"); // 100/3 = 33.33...
        BigDecimal expectedShareAmount = new BigDecimal("100.00")
            .divide(new BigDecimal(3), 2, RoundingMode.HALF_UP);

        doNothing().when(participantService).validateUsersAreParticipants(tripId, userIds);
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user1));
        when(expenseShareDAL.saveShare(any(ExpenseShare.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        List<ExpenseShare> result = expenseShareService.createExpenseShares(expenseId, tripId, userIds, totalAmount);

        // Assert
        assertEquals(3, result.size());
        assertEquals(0, result.get(0).getAmount().compareTo(expectedShareAmount));
        verify(expenseShareDAL, times(3)).saveShare(any(ExpenseShare.class));
    }

    @Test
    void createExpenseShares_WithNonParticipants_ShouldThrowException() {
        // Arrange
        Integer expenseId = 100;
        Integer tripId = 1;
        List<Long> userIds = Arrays.asList(1L, 2L);
        BigDecimal totalAmount = new BigDecimal("100.00");

        doThrow(new IllegalArgumentException("User is not a participant"))
            .when(participantService).validateUsersAreParticipants(tripId, userIds);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseShareService.createExpenseShares(expenseId, tripId, userIds, totalAmount)
        );

        assertEquals("User is not a participant", exception.getMessage());
        verify(participantService, times(1)).validateUsersAreParticipants(tripId, userIds);
        verify(expenseShareDAL, never()).saveShare(any(ExpenseShare.class));
    }

    @Test
    void createExpenseShares_WithNonExistentUser_ShouldThrowException() {
        // Arrange
        Integer expenseId = 100;
        Integer tripId = 1;
        List<Long> userIds = Arrays.asList(1L, 999L);
        BigDecimal totalAmount = new BigDecimal("100.00");

        doNothing().when(participantService).validateUsersAreParticipants(tripId, userIds);
        when(userService.getUserById(1L)).thenReturn(Optional.of(user1));
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseShareService.createExpenseShares(expenseId, tripId, userIds, totalAmount)
        );

        assertEquals("User not found: 999", exception.getMessage());
        verify(userService, times(2)).getUserById(anyLong());
        verify(expenseShareDAL, times(1)).saveShare(any(ExpenseShare.class)); // Only first user saved
    }

    @Test
    void createCustomExpenseShares_WithValidAmounts_ShouldCreateShares() {
        // Arrange
        Integer expenseId = 100;
        Integer tripId = 1;
        Map<Long, BigDecimal> userAmounts = new HashMap<>();
        userAmounts.put(1L, new BigDecimal("30.00"));
        userAmounts.put(2L, new BigDecimal("70.00"));

        doNothing().when(participantService).validateUsersAreParticipants(eq(tripId), anyList());
        when(userService.getUserById(1L)).thenReturn(Optional.of(user1));
        when(userService.getUserById(2L)).thenReturn(Optional.of(user2));

        when(expenseShareDAL.saveShare(any(ExpenseShare.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        List<ExpenseShare> result = expenseShareService.createCustomExpenseShares(expenseId, tripId, userAmounts);

        // Assert
        assertEquals(2, result.size());
        verify(participantService, times(1)).validateUsersAreParticipants(eq(tripId), anyList());
        verify(expenseShareDAL, times(2)).saveShare(any(ExpenseShare.class));
    }

    @Test
    void createCustomExpenseShares_WithNonParticipants_ShouldThrowException() {
        // Arrange
        Integer expenseId = 100;
        Integer tripId = 1;
        Map<Long, BigDecimal> userAmounts = new HashMap<>();
        userAmounts.put(1L, new BigDecimal("50.00"));

        doThrow(new IllegalArgumentException("User is not a participant"))
            .when(participantService).validateUsersAreParticipants(eq(tripId), anyList());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseShareService.createCustomExpenseShares(expenseId, tripId, userAmounts)
        );

        assertEquals("User is not a participant", exception.getMessage());
        verify(expenseShareDAL, never()).saveShare(any(ExpenseShare.class));
    }

    @Test
    void createCustomExpenseShares_WithNonExistentUser_ShouldThrowException() {
        // Arrange
        Integer expenseId = 100;
        Integer tripId = 1;
        Map<Long, BigDecimal> userAmounts = new HashMap<>();
        userAmounts.put(999L, new BigDecimal("50.00"));

        doNothing().when(participantService).validateUsersAreParticipants(eq(tripId), anyList());
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseShareService.createCustomExpenseShares(expenseId, tripId, userAmounts)
        );

        assertEquals("User not found: 999", exception.getMessage());
        verify(expenseShareDAL, never()).saveShare(any(ExpenseShare.class));
    }

    @Test
    void getExpenseSharesWithUserDetails_ShouldReturnSharesWithUsers() {
        // Arrange
        Integer expenseId = 100;
        List<ExpenseShare> expectedShares = Arrays.asList(share1, share2);

        when(expenseShareDAL.getSharesByExpenseIdWithUserDetails(expenseId))
            .thenReturn(expectedShares);

        // Act
        List<ExpenseShare> result = expenseShareService.getExpenseSharesWithUserDetails(expenseId);

        // Assert
        assertEquals(expectedShares, result);
        assertEquals(2, result.size());
        verify(expenseShareDAL, times(1)).getSharesByExpenseIdWithUserDetails(expenseId);
    }

    @Test
    void getExpenseSharesWithUserDetails_WhenNoShares_ShouldReturnEmptyList() {
        // Arrange
        Integer expenseId = 999;
        when(expenseShareDAL.getSharesByExpenseIdWithUserDetails(expenseId))
            .thenReturn(Collections.emptyList());

        // Act
        List<ExpenseShare> result = expenseShareService.getExpenseSharesWithUserDetails(expenseId);

        // Assert
        assertTrue(result.isEmpty());
        verify(expenseShareDAL, times(1)).getSharesByExpenseIdWithUserDetails(expenseId);
    }

    @Test
    void getExpenseShareDetailsMap_ShouldReturnMappedDetails() {
        // Arrange
        Integer expenseId = 100;
        List<ExpenseShare> shares = Arrays.asList(share1, share2);
        Map<Long, ExpenseShareMapper.ShareDetails> expectedMap = new HashMap<>();

        when(expenseShareDAL.getSharesByExpenseIdWithUserDetails(expenseId)).thenReturn(shares);
        when(expenseShareMapper.toDetailedSharesMap(shares)).thenReturn(expectedMap);

        // Act
        Map<Long, ExpenseShareMapper.ShareDetails> result = expenseShareService.getExpenseShareDetailsMap(expenseId);

        // Assert
        assertEquals(expectedMap, result);
        verify(expenseShareDAL, times(1)).getSharesByExpenseIdWithUserDetails(expenseId);
        verify(expenseShareMapper, times(1)).toDetailedSharesMap(shares);
    }

    @Test
    void calculateOutstandingBalances_ShouldAggregateAmountsByUser() {
        // Arrange
        Long userId = 1L;
        ExpenseShare unsettledShare1 = new ExpenseShare(100, userId, new BigDecimal("30.00"));
        ExpenseShare unsettledShare2 = new ExpenseShare(101, userId, new BigDecimal("20.00"));
        ExpenseShare unsettledShare3 = new ExpenseShare(102, userId, new BigDecimal("50.00"));

        when(expenseShareDAL.getUnsettledSharesByUserId(userId))
            .thenReturn(Arrays.asList(unsettledShare1, unsettledShare2, unsettledShare3));

        // Act
        Map<Long, BigDecimal> result = expenseShareService.calculateOutstandingBalances(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey(userId));
        assertEquals(0, result.get(userId).compareTo(new BigDecimal("100.00")));
        verify(expenseShareDAL, times(1)).getUnsettledSharesByUserId(userId);
    }

    @Test
    void calculateOutstandingBalances_WhenNoUnsettledShares_ShouldReturnEmptyMap() {
        // Arrange
        Long userId = 1L;
        when(expenseShareDAL.getUnsettledSharesByUserId(userId))
            .thenReturn(Collections.emptyList());

        // Act
        Map<Long, BigDecimal> result = expenseShareService.calculateOutstandingBalances(userId);

        // Assert
        assertTrue(result.isEmpty());
        verify(expenseShareDAL, times(1)).getUnsettledSharesByUserId(userId);
    }

    @Test
    void settleExpenseShare_WhenShareExists_ShouldMarkAsSettled() {
        // Arrange
        Integer shareId = 1;
        ExpenseShare unsettledShare = new ExpenseShare(100, 1L, new BigDecimal("50.00"));
        unsettledShare.setId(shareId);
        unsettledShare.setIsSettled(false);

        when(expenseShareDAL.getSharesByExpenseId(shareId))
            .thenReturn(Collections.singletonList(unsettledShare));
        when(expenseShareDAL.saveShare(any(ExpenseShare.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ExpenseShare result = expenseShareService.settleExpenseShare(shareId);

        // Assert
        assertTrue(result.getIsSettled());
        verify(expenseShareDAL, times(1)).getSharesByExpenseId(shareId);
        verify(expenseShareDAL, times(1)).saveShare(unsettledShare);
    }

    @Test
    void settleExpenseShare_WhenShareNotFound_ShouldThrowException() {
        // Arrange
        Integer shareId = 999;
        when(expenseShareDAL.getSharesByExpenseId(shareId))
            .thenReturn(Collections.emptyList());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> expenseShareService.settleExpenseShare(shareId)
        );

        assertEquals("Share not found with id: 999", exception.getMessage());
        verify(expenseShareDAL, times(1)).getSharesByExpenseId(shareId);
        verify(expenseShareDAL, never()).saveShare(any(ExpenseShare.class));
    }

    @Test
    void getUnsettledSharesForUser_ShouldReturnUnsettledShares() {
        // Arrange
        Long userId = 1L;
        List<ExpenseShare> unsettledShares = Arrays.asList(share1, share2);

        when(expenseShareDAL.getUnsettledSharesByUserId(userId)).thenReturn(unsettledShares);

        // Act
        List<ExpenseShare> result = expenseShareService.getUnsettledSharesForUser(userId);

        // Assert
        assertEquals(unsettledShares, result);
        assertEquals(2, result.size());
        verify(expenseShareDAL, times(1)).getUnsettledSharesByUserId(userId);
    }

    @Test
    void getUnsettledSharesForUser_WhenAllSettled_ShouldReturnEmptyList() {
        // Arrange
        Long userId = 1L;
        when(expenseShareDAL.getUnsettledSharesByUserId(userId))
            .thenReturn(Collections.emptyList());

        // Act
        List<ExpenseShare> result = expenseShareService.getUnsettledSharesForUser(userId);

        // Assert
        assertTrue(result.isEmpty());
        verify(expenseShareDAL, times(1)).getUnsettledSharesByUserId(userId);
    }

    @Test
    void getUserShareSummary_ShouldCalculateTotals() {
        // Arrange
        Long userId = 1L;

        ExpenseShare share1 = new ExpenseShare(100, userId, new BigDecimal("30.00"));
        share1.setIsSettled(false);
        ExpenseShare share2 = new ExpenseShare(101, userId, new BigDecimal("20.00"));
        share2.setIsSettled(true);
        ExpenseShare share3 = new ExpenseShare(102, userId, new BigDecimal("50.00"));
        share3.setIsSettled(false);

        List<ExpenseShare> allShares = Arrays.asList(share1, share2, share3);
        List<ExpenseShare> unsettledShares = Arrays.asList(share1, share3);

        when(expenseShareDAL.getSharesByUserId(userId)).thenReturn(allShares);
        when(expenseShareDAL.getUnsettledSharesByUserId(userId)).thenReturn(unsettledShares);

        // Act
        ExpenseShareService.UserShareSummary result = expenseShareService.getUserShareSummary(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(0, result.getTotalOwed().compareTo(new BigDecimal("100.00"))); // 30+20+50
        assertEquals(0, result.getTotalUnsettled().compareTo(new BigDecimal("80.00"))); // 30+50
        assertEquals(3, result.getTotalSharesCount());
        assertEquals(2, result.getUnsettledSharesCount());

        verify(expenseShareDAL, times(1)).getSharesByUserId(userId);
        verify(expenseShareDAL, times(1)).getUnsettledSharesByUserId(userId);
    }

    @Test
    void getUserShareSummary_WithNoShares_ShouldReturnZeroTotals() {
        // Arrange
        Long userId = 1L;
        when(expenseShareDAL.getSharesByUserId(userId)).thenReturn(Collections.emptyList());
        when(expenseShareDAL.getUnsettledSharesByUserId(userId)).thenReturn(Collections.emptyList());

        // Act
        ExpenseShareService.UserShareSummary result = expenseShareService.getUserShareSummary(userId);

        // Assert
        assertEquals(userId, result.getUserId());
        assertEquals(0, result.getTotalOwed().compareTo(BigDecimal.ZERO));
        assertEquals(0, result.getTotalUnsettled().compareTo(BigDecimal.ZERO));
        assertEquals(0, result.getTotalSharesCount());
        assertEquals(0, result.getUnsettledSharesCount());
    }

    @Test
    void deleteSharesForExpense_ShouldDelegateToDAL() {
        // Arrange
        Integer expenseId = 100;
        doNothing().when(expenseShareDAL).deleteSharesByExpenseId(expenseId);

        // Act
        expenseShareService.deleteSharesForExpense(expenseId);

        // Assert
        verify(expenseShareDAL, times(1)).deleteSharesByExpenseId(expenseId);
    }

    @Test
    void userShareSummary_GettersShouldReturnCorrectValues() {
        // Arrange
        Long userId = 1L;
        BigDecimal totalOwed = new BigDecimal("150.00");
        BigDecimal totalUnsettled = new BigDecimal("100.00");
        Integer totalSharesCount = 5;
        Integer unsettledSharesCount = 3;

        // Act
        ExpenseShareService.UserShareSummary summary = new ExpenseShareService.UserShareSummary(
            userId, totalOwed, totalUnsettled, totalSharesCount, unsettledSharesCount
        );

        // Assert
        assertEquals(userId, summary.getUserId());
        assertEquals(totalOwed, summary.getTotalOwed());
        assertEquals(totalUnsettled, summary.getTotalUnsettled());
        assertEquals(totalSharesCount, summary.getTotalSharesCount());
        assertEquals(unsettledSharesCount, summary.getUnsettledSharesCount());
    }

    @Test
    void createExpenseShares_WithSingleUser_ShouldCreateOneShare() {
        // Arrange
        Integer expenseId = 100;
        Integer tripId = 1;
        List<Long> userIds = Collections.singletonList(1L);
        BigDecimal totalAmount = new BigDecimal("100.00");

        doNothing().when(participantService).validateUsersAreParticipants(tripId, userIds);
        when(userService.getUserById(1L)).thenReturn(Optional.of(user1));
        when(expenseShareDAL.saveShare(any(ExpenseShare.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        List<ExpenseShare> result = expenseShareService.createExpenseShares(expenseId, tripId, userIds, totalAmount);

        // Assert
        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getAmount().compareTo(new BigDecimal("100.00")));
        verify(expenseShareDAL, times(1)).saveShare(any(ExpenseShare.class));
    }

    @Test
    void createCustomExpenseShares_WithEmptyMap_ShouldNotCreateShares() {
        // Arrange
        Integer expenseId = 100;
        Integer tripId = 1;
        Map<Long, BigDecimal> userAmounts = new HashMap<>();

        doNothing().when(participantService).validateUsersAreParticipants(eq(tripId), anyList());

        // Act
        List<ExpenseShare> result = expenseShareService.createCustomExpenseShares(expenseId, tripId, userAmounts);

        // Assert
        assertTrue(result.isEmpty());
        verify(expenseShareDAL, never()).saveShare(any(ExpenseShare.class));
    }
}

