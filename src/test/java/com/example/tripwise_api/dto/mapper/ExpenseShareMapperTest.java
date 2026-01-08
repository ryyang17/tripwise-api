package com.example.tripwise_api.dto.mapper;

import com.example.tripwise_api.model.ExpenseShare;
import com.example.tripwise_api.model.User;
import com.example.tripwise_api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseShareMapperTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private ExpenseShareMapper expenseShareMapper;

    private ExpenseShare share1;
    private ExpenseShare share2;
    private User user1;
    private User user2;
    private List<ExpenseShare> shares;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");

        share1 = new ExpenseShare();
        share1.setId(1);
        share1.setExpenseId(100);
        share1.setUserId(1L);
        share1.setUser(user1);
        share1.setAmount(new BigDecimal("50.00"));
        share1.setIsSettled(false);

        share2 = new ExpenseShare();
        share2.setId(2);
        share2.setExpenseId(100);
        share2.setUserId(2L);
        share2.setUser(user2);
        share2.setAmount(new BigDecimal("75.50"));
        share2.setIsSettled(true);

        shares = new ArrayList<>();
        shares.add(share1);
        shares.add(share2);
    }

    @Test
    void toUserSharesMap_ShouldConvertToSimpleMap() {
        // Act
        Map<Long, BigDecimal> result = expenseShareMapper.toUserSharesMap(shares);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(new BigDecimal("50.00"), result.get(1L));
        assertEquals(new BigDecimal("75.50"), result.get(2L));
    }

    @Test
    void toUserSharesMap_WithNullList_ShouldReturnEmptyMap() {
        // Act
        Map<Long, BigDecimal> result = expenseShareMapper.toUserSharesMap(null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toUserSharesMap_WithEmptyList_ShouldReturnEmptyMap() {
        // Act
        Map<Long, BigDecimal> result = expenseShareMapper.toUserSharesMap(new ArrayList<>());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toDetailedSharesMap_WithUserRelationship_ShouldIncludeUserDetails() {
        // Act
        Map<Long, ExpenseShareMapper.ShareDetails> result = expenseShareMapper.toDetailedSharesMap(shares);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        ExpenseShareMapper.ShareDetails details1 = result.get(1L);
        assertNotNull(details1);
        assertEquals(1L, details1.getUserId());
        assertEquals("user1", details1.getUserName());
        assertEquals("user1@example.com", details1.getUserEmail());
        assertEquals(new BigDecimal("50.00"), details1.getAmount());
        assertFalse(details1.getIsSettled());

        ExpenseShareMapper.ShareDetails details2 = result.get(2L);
        assertNotNull(details2);
        assertEquals(2L, details2.getUserId());
        assertEquals("user2", details2.getUserName());
        assertEquals("user2@example.com", details2.getUserEmail());
        assertEquals(new BigDecimal("75.50"), details2.getAmount());
        assertTrue(details2.getIsSettled());
    }

    @Test
    void toDetailedSharesMap_WithoutUserRelationship_ShouldFetchUserViaService() {
        // Arrange
        share1.setUser(null);
        share2.setUser(null);
        when(userService.getUserById(1L)).thenReturn(Optional.of(user1));
        when(userService.getUserById(2L)).thenReturn(Optional.of(user2));

        // Act
        Map<Long, ExpenseShareMapper.ShareDetails> result = expenseShareMapper.toDetailedSharesMap(shares);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        ExpenseShareMapper.ShareDetails details1 = result.get(1L);
        assertNotNull(details1);
        assertEquals("user1", details1.getUserName());
        assertEquals("user1@example.com", details1.getUserEmail());
    }

    @Test
    void toDetailedSharesMap_WithUserNotFound_ShouldHandleGracefully() {
        // Arrange
        share1.setUser(null);
        share2.setUser(null);
        when(userService.getUserById(1L)).thenReturn(Optional.empty());
        when(userService.getUserById(2L)).thenReturn(Optional.of(user2));

        // Act
        Map<Long, ExpenseShareMapper.ShareDetails> result = expenseShareMapper.toDetailedSharesMap(shares);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        ExpenseShareMapper.ShareDetails details1 = result.get(1L);
        assertNotNull(details1);
        assertEquals(1L, details1.getUserId());
        assertNull(details1.getUserName());
        assertNull(details1.getUserEmail());
        assertEquals(new BigDecimal("50.00"), details1.getAmount());
    }

    @Test
    void toDetailedSharesMap_WithNullList_ShouldReturnEmptyMap() {
        // Act
        Map<Long, ExpenseShareMapper.ShareDetails> result = expenseShareMapper.toDetailedSharesMap(null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toDetailedSharesMap_WithEmptyList_ShouldReturnEmptyMap() {
        // Act
        Map<Long, ExpenseShareMapper.ShareDetails> result = expenseShareMapper.toDetailedSharesMap(new ArrayList<>());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toDetailedSharesMap_WithMultipleSharesSameUser_ShouldKeepLastOne() {
        // Arrange
        ExpenseShare share3 = new ExpenseShare();
        share3.setId(3);
        share3.setExpenseId(100);
        share3.setUserId(1L);
        share3.setUser(user1);
        share3.setAmount(new BigDecimal("100.00"));
        share3.setIsSettled(true);

        shares.add(share3);

        // Act
        Map<Long, ExpenseShareMapper.ShareDetails> result = expenseShareMapper.toDetailedSharesMap(shares);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size()); // Still only 2 unique users

        ExpenseShareMapper.ShareDetails details1 = result.get(1L);
        // The last share for user 1 should be in the map
        assertEquals(new BigDecimal("100.00"), details1.getAmount());
        assertTrue(details1.getIsSettled());
    }

    @Test
    void shareDetails_GettersAndSetters_ShouldWorkCorrectly() {
        // Arrange
        ExpenseShareMapper.ShareDetails details = new ExpenseShareMapper.ShareDetails();

        // Act
        details.setUserId(10L);
        details.setUserName("testuser");
        details.setUserEmail("test@example.com");
        details.setAmount(new BigDecimal("123.45"));
        details.setIsSettled(true);

        // Assert
        assertEquals(10L, details.getUserId());
        assertEquals("testuser", details.getUserName());
        assertEquals("test@example.com", details.getUserEmail());
        assertEquals(new BigDecimal("123.45"), details.getAmount());
        assertTrue(details.getIsSettled());
    }

    @Test
    void toUserSharesMap_WithZeroAmount_ShouldIncludeShare() {
        // Arrange
        share1.setAmount(BigDecimal.ZERO);

        // Act
        Map<Long, BigDecimal> result = expenseShareMapper.toUserSharesMap(shares);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(BigDecimal.ZERO, result.get(1L));
    }

    @Test
    void toDetailedSharesMap_WithNegativeAmount_ShouldIncludeShare() {
        // Arrange
        share1.setAmount(new BigDecimal("-10.00"));

        // Act
        Map<Long, ExpenseShareMapper.ShareDetails> result = expenseShareMapper.toDetailedSharesMap(shares);

        // Assert
        assertNotNull(result);
        ExpenseShareMapper.ShareDetails details1 = result.get(1L);
        assertEquals(new BigDecimal("-10.00"), details1.getAmount());
    }
}

