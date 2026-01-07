package com.example.tripwise_api.service;

import com.example.tripwise_api.dal.ExpenseDAL;
import com.example.tripwise_api.dal.ExpenseShareDAL;
import com.example.tripwise_api.dto.request.SharedExpenseRequest;
import com.example.tripwise_api.model.Expense;
import com.example.tripwise_api.model.ExpenseCategory;
import com.example.tripwise_api.model.ExpenseShare;
import com.example.tripwise_api.model.TripParticipant;
import com.example.tripwise_api.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseDAL expenseDAL;
    @Mock
    private CurrencyService currencyService;
    @Mock
    private ExpenseShareDAL expenseShareDAL;
    @Mock
    private ExpenseCategoryService expenseCategoryService;
    @Mock
    private TripParticipantService tripParticipantService;
    @Mock
    private UserService userService;

    @InjectMocks
    private ExpenseService expenseService;

    private Expense sampleExpense;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleExpense = new Expense();
        sampleExpense.setExpenseId(1);
        sampleExpense.setTripId(100);
        sampleExpense.setDescription("Dinner");
        sampleExpense.setAmount(new BigDecimal("60.00"));
        sampleExpense.setOriginalAmount(new BigDecimal("60.00"));
        sampleExpense.setCurrency("EUR");
        sampleExpense.setExchangeRate(BigDecimal.ONE);
        sampleExpense.setExpenseDate(LocalDate.of(2024, 6, 10));
        sampleExpense.setPaidBy(10L);
        sampleExpense.setValidated(false);

        sampleUser = new User();
        sampleUser.setId(10L);
        sampleUser.setUsername("payer");
    }

    @Test
    void getAllExpenses_ShouldReturnAll() {
        List<Expense> expected = List.of(sampleExpense);
        when(expenseDAL.getAllExpenses()).thenReturn(expected);

        List<Expense> result = expenseService.getAllExpenses();

        assertEquals(expected, result);
        verify(expenseDAL, times(1)).getAllExpenses();
    }

    @Test
    void getExpensesByTripId_ShouldPopulateSharedFields() {
        when(expenseDAL.getExpensesByTripId(100)).thenReturn(List.of(sampleExpense));
        // shares returned with user details
        ExpenseShare share1 = new ExpenseShare(sampleExpense.getExpenseId(), 10L, new BigDecimal("30.00"));
        share1.setUser(sampleUser);
        ExpenseShare share2 = new ExpenseShare(sampleExpense.getExpenseId(), 20L, new BigDecimal("30.00"));
        User user2 = new User();
        user2.setId(20L);
        share2.setUser(user2);

        when(expenseShareDAL.getSharesByExpenseIdWithUserDetails(sampleExpense.getExpenseId()))
            .thenReturn(List.of(share1, share2));

        // map userId -> participantId
        TripParticipant p1 = new TripParticipant();
        p1.setId(1000);
        p1.setTripId(sampleExpense.getTripId());
        p1.setUserId(10L);
        TripParticipant p2 = new TripParticipant();
        p2.setId(1001);
        p2.setTripId(sampleExpense.getTripId());
        p2.setUserId(20L);
        when(tripParticipantService.getParticipantsByTripIdWithUserDetails(sampleExpense.getTripId()))
            .thenReturn(List.of(p1, p2));

        List<Expense> result = expenseService.getExpensesByTripId(100);

        assertEquals(1, result.size());
        Expense e = result.get(0);
        assertTrue(e.getIsSharedExpense());
        assertEquals(List.of(1000, 1001), e.getSplitWith());
        assertEquals(1000, e.getPaidById());
        assertTrue(e.getSplitEvenly());
        assertEquals(2, e.getCustomSplits().size());
        verify(expenseDAL).getExpensesByTripId(100);
        verify(expenseShareDAL).getSharesByExpenseIdWithUserDetails(sampleExpense.getExpenseId());
        verify(tripParticipantService).getParticipantsByTripIdWithUserDetails(sampleExpense.getTripId());
    }

    @Test
    void getExpensesByTripIdWithCurrency_ShouldConvertAmounts() {
        Expense e1 = new Expense();
        e1.setExpenseId(2);
        e1.setTripId(100);
        e1.setCurrency("EUR");
        e1.setOriginalAmount(new BigDecimal("50.00"));
        e1.setAmount(new BigDecimal("50.00"));

        when(expenseDAL.getExpensesByTripId(100)).thenReturn(List.of(e1));
        when(currencyService.getExchangeRate("EUR", "USD")).thenReturn(new BigDecimal("1.2"));

        // no shares
        when(expenseShareDAL.getSharesByExpenseIdWithUserDetails(2)).thenReturn(Collections.emptyList());

        List<Expense> result = expenseService.getExpensesByTripIdWithCurrency(100, "USD");

        assertEquals(1, result.size());
        // Use compareTo for scale-agnostic equality
        assertEquals(0, result.get(0).getAmount().compareTo(new BigDecimal("60.0")));
        assertFalse(result.get(0).getIsSharedExpense());
        verify(currencyService).getExchangeRate("EUR", "USD");
    }

    @Test
    void getExpenseById_ShouldPopulateSharedFields_WhenSharesExist() {
        when(expenseDAL.getExpenseById(1)).thenReturn(Optional.of(sampleExpense));
        ExpenseShare share = new ExpenseShare(sampleExpense.getExpenseId(), 10L, new BigDecimal("60.00"));
        share.setUser(sampleUser);
        when(expenseShareDAL.getSharesByExpenseIdWithUserDetails(1)).thenReturn(List.of(share));
        TripParticipant p1 = new TripParticipant();
        p1.setId(1000);
        p1.setTripId(sampleExpense.getTripId());
        p1.setUserId(10L);
        when(tripParticipantService.getParticipantsByTripIdWithUserDetails(sampleExpense.getTripId()))
            .thenReturn(List.of(p1));

        Optional<Expense> result = expenseService.getExpenseById(1);
        assertTrue(result.isPresent());
        assertTrue(result.get().getIsSharedExpense());
        assertEquals(List.of(1000), result.get().getSplitWith());
        assertEquals(1000, result.get().getPaidById());
    }

    @Test
    void createExpense_ShouldSaveAndCreateEvenShares_WhenSplitWithProvided() {
        Expense input = new Expense();
        input.setDescription("Lunch");
        input.setAmount(new BigDecimal("90.00"));
        input.setCurrency("EUR");
        input.setCategory(new ExpenseCategory());
        input.setExpenseDate(LocalDate.now());
        input.setPaidBy(10L);
        input.setSplitWithUserIds(Arrays.asList(20L, 30L));

        Expense saved = new Expense();
        saved.setExpenseId(5);
        saved.setTripId(100);
        saved.setAmount(new BigDecimal("90.00"));
        saved.setPaidBy(10L);

        when(expenseDAL.saveExpense(any(Expense.class))).thenReturn(saved);
        when(userService.getUserById(10L)).thenReturn(Optional.of(sampleUser));
        User u2 = new User(); u2.setId(20L);
        User u3 = new User(); u3.setId(30L);
        when(userService.getUserById(20L)).thenReturn(Optional.of(u2));
        when(userService.getUserById(30L)).thenReturn(Optional.of(u3));

        Expense result = expenseService.createExpense(100, input);

        assertNotNull(result);
        verify(expenseDAL).saveExpense(any(Expense.class));
        // participants: 10,20,30 -> 3 shares, 90/3 = 30 each
        verify(expenseShareDAL, times(3)).saveShare(any(ExpenseShare.class));
    }

    @Test
    void createExpenseWithCurrency_ShouldConvertAndDelegateToCreateExpense() {
        Expense input = new Expense();
        input.setAmount(new BigDecimal("100.00"));
        input.setCurrency("EUR");
        when(currencyService.getExchangeRate("EUR", "USD")).thenReturn(new BigDecimal("1.1"));

        Expense saved = new Expense();
        saved.setExpenseId(6);
        saved.setTripId(100);
        saved.setAmount(new BigDecimal("110.00"));
        saved.setOriginalAmount(new BigDecimal("100.00"));
        saved.setExchangeRate(new BigDecimal("1.1"));
        when(expenseDAL.saveExpense(any(Expense.class))).thenReturn(saved);
        // no shares created
        Expense result = expenseService.createExpenseWithCurrency(100, input, "USD");

        assertEquals(new BigDecimal("110.00"), result.getAmount());
        assertEquals(new BigDecimal("100.00"), result.getOriginalAmount());
        assertEquals(new BigDecimal("1.1"), result.getExchangeRate());
        verify(currencyService).getExchangeRate("EUR", "USD");
    }

    @Test
    void updateExpense_ShouldUpdateAndRecreateShares() {
        Expense existing = new Expense();
        existing.setExpenseId(7);
        existing.setTripId(100);
        existing.setAmount(new BigDecimal("60.00"));
        existing.setPaidBy(10L);

        Expense update = new Expense();
        update.setDescription("Updated");
        update.setAmount(new BigDecimal("80.00"));
        update.setCategory(new ExpenseCategory());
        update.setExpenseDate(LocalDate.now());
        update.setReceiptUrl("url");
        update.setValidated(true);
        update.setOriginalAmount(new BigDecimal("80.00"));
        update.setCurrency("EUR");
        update.setExchangeRate(BigDecimal.ONE);
        update.setPaidBy(10L);
        update.setSplitWithUserIds(Collections.singletonList(20L));

        when(expenseDAL.getExpenseById(7)).thenReturn(Optional.of(existing));
        when(expenseDAL.saveExpense(any(Expense.class))).thenReturn(existing);
        User u1 = new User(); u1.setId(10L);
        User u2 = new User(); u2.setId(20L);
        when(userService.getUserById(10L)).thenReturn(Optional.of(u1));
        when(userService.getUserById(20L)).thenReturn(Optional.of(u2));

        Expense result = expenseService.updateExpense(7, update);
        assertNotNull(result);
        verify(expenseShareDAL).deleteSharesByExpenseId(7);
        verify(expenseShareDAL, times(2)).saveShare(any(ExpenseShare.class));
    }

    @Test
    void deleteExpense_ShouldDeleteSharesAndExpense_WhenExists() {
        when(expenseDAL.existsById(9)).thenReturn(true);

        expenseService.deleteExpense(9);

        verify(expenseDAL).existsById(9);
        verify(expenseShareDAL).deleteSharesByExpenseId(9);
        verify(expenseDAL).deleteExpense(9);
    }

    @Test
    void deleteExpense_ShouldThrow_WhenNotExists() {
        when(expenseDAL.existsById(999)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> expenseService.deleteExpense(999));
        assertEquals("Expense not found with id: 999", ex.getMessage());
        verify(expenseDAL).existsById(999);
        verify(expenseDAL, never()).deleteExpense(anyInt());
    }

    @Test
    void createSharedExpense_EvenSplit_ShouldCreateSharesAndPopulateFields() {
        SharedExpenseRequest req = new SharedExpenseRequest();
        req.setDescription("Shared dinner");
        req.setAmount(new BigDecimal("120.00"));
        req.setCategoryId(1);
        req.setCurrency("EUR");
        req.setPaidByParticipantId(1000);
        req.setSplitWithParticipantIds(Arrays.asList(1000, 1001)); // include payer for simplicity
        req.setSplitEvenly(true);

        ExpenseCategory cat = new ExpenseCategory();
        when(expenseCategoryService.getCategoryById(1L)).thenReturn(Optional.of(cat));

        TripParticipant payer = new TripParticipant();
        payer.setId(1000);
        payer.setTripId(100);
        payer.setUserId(10L);
        when(tripParticipantService.getParticipantById(1000)).thenReturn(Optional.of(payer));

        Expense saved = new Expense();
        saved.setExpenseId(20);
        saved.setTripId(100);
        saved.setAmount(new BigDecimal("120.00"));
        saved.setPaidBy(10L);
        when(expenseDAL.saveExpense(any(Expense.class))).thenReturn(saved);

        // Participants mapping & users
        TripParticipant p1 = payer;
        TripParticipant p2 = new TripParticipant();
        p2.setId(1001); p2.setTripId(100); p2.setUserId(20L);
        when(tripParticipantService.getParticipantById(1001)).thenReturn(Optional.of(p2));
        User u1 = new User(); u1.setId(10L);
        User u2 = new User(); u2.setId(20L);
        when(userService.getUserById(10L)).thenReturn(Optional.of(u1));
        when(userService.getUserById(20L)).thenReturn(Optional.of(u2));
        when(expenseShareDAL.getSharesByExpenseIdWithUserDetails(20)).thenReturn(Arrays.asList(
            new ExpenseShare(20, 10L, new BigDecimal("60.00")),
            new ExpenseShare(20, 20L, new BigDecimal("60.00"))
        ));
        when(tripParticipantService.getParticipantsByTripIdWithUserDetails(100)).thenReturn(List.of(p1, p2));

        Expense result = expenseService.createSharedExpense(100, req);

        assertNotNull(result);
        verify(expenseShareDAL, times(2)).saveShare(any(ExpenseShare.class));
        // populated shared fields
        assertTrue(result.getIsSharedExpense());
        assertTrue(result.getSplitEvenly());
        assertEquals(2, result.getSplitWith().size());
    }

    @Test
    void createSharedExpense_CustomSplit_ShouldValidateTotals() {
        SharedExpenseRequest req = new SharedExpenseRequest();
        req.setDescription("Taxi");
        req.setAmount(new BigDecimal("100.00"));
        req.setCategoryId(1);
        req.setCurrency("EUR");
        req.setPaidByParticipantId(2000);
        req.setSplitWithParticipantIds(Arrays.asList(2000, 2001));
        req.setSplitEvenly(false);
        Map<String, BigDecimal> custom = new HashMap<>();
        custom.put("2000", new BigDecimal("40.00"));
        custom.put("2001", new BigDecimal("60.00"));
        req.setCustomSplits(custom);

        ExpenseCategory cat = new ExpenseCategory();
        when(expenseCategoryService.getCategoryById(1L)).thenReturn(Optional.of(cat));

        TripParticipant payer = new TripParticipant();
        payer.setId(2000); payer.setTripId(100); payer.setUserId(10L);
        TripParticipant other = new TripParticipant();
        other.setId(2001); other.setTripId(100); other.setUserId(20L);
        when(tripParticipantService.getParticipantById(2000)).thenReturn(Optional.of(payer));
        when(tripParticipantService.getParticipantById(2001)).thenReturn(Optional.of(other));
        User u1 = new User(); u1.setId(10L);
        User u2 = new User(); u2.setId(20L);
        when(userService.getUserById(10L)).thenReturn(Optional.of(u1));
        when(userService.getUserById(20L)).thenReturn(Optional.of(u2));
        Expense saved = new Expense();
        saved.setExpenseId(80);
        saved.setTripId(100);
        saved.setAmount(new BigDecimal("100.00"));
        saved.setPaidBy(10L);
        when(expenseDAL.saveExpense(any(Expense.class))).thenReturn(saved);

        assertDoesNotThrow(() -> expenseService.createSharedExpense(100, req));
    }

    @Test
    void createSharedExpense_ShouldRejectParticipantFromOtherTrip() {
        SharedExpenseRequest req = new SharedExpenseRequest();
        req.setDescription("Bad case");
        req.setAmount(new BigDecimal("10.00"));
        req.setCategoryId(1);
        req.setCurrency("EUR");
        req.setPaidByParticipantId(3000);
        req.setSplitWithParticipantIds(Collections.singletonList(3001));
        req.setSplitEvenly(true);

        when(expenseCategoryService.getCategoryById(1L)).thenReturn(Optional.of(new ExpenseCategory()));
        TripParticipant payer = new TripParticipant();
        payer.setId(3000); payer.setTripId(999); payer.setUserId(10L); // different trip
        when(tripParticipantService.getParticipantById(3000)).thenReturn(Optional.of(payer));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> expenseService.createSharedExpense(100, req));
        assertEquals("Payer participant does not belong to this trip", ex.getMessage());
    }

    @Test
    void updateSharedExpense_ShouldDeleteOldSharesAndCreateNew() {
        SharedExpenseRequest req = new SharedExpenseRequest();
        req.setDescription("Update shared");
        req.setAmount(new BigDecimal("80.00"));
        req.setCategoryId(1);
        req.setCurrency("EUR");
        req.setPaidByParticipantId(4000);
        req.setSplitWithParticipantIds(Arrays.asList(4000, 4001));
        req.setSplitEvenly(true);

        Expense existing = new Expense();
        existing.setExpenseId(40); existing.setTripId(100); existing.setPaidBy(10L); existing.setAmount(new BigDecimal("50.00"));
        when(expenseDAL.getExpenseById(40)).thenReturn(Optional.of(existing));

        ExpenseCategory cat = new ExpenseCategory();
        when(expenseCategoryService.getCategoryById(1L)).thenReturn(Optional.of(cat));
        TripParticipant payer = new TripParticipant();
        payer.setId(4000); payer.setTripId(100); payer.setUserId(10L);
        when(tripParticipantService.getParticipantById(4000)).thenReturn(Optional.of(payer));
        TripParticipant other = new TripParticipant();
        other.setId(4001); other.setTripId(100); other.setUserId(20L);
        when(tripParticipantService.getParticipantById(4001)).thenReturn(Optional.of(other));

        Expense saved = new Expense();
        saved.setExpenseId(40); saved.setTripId(100); saved.setAmount(new BigDecimal("80.00")); saved.setPaidBy(10L);
        when(expenseDAL.saveExpense(any(Expense.class))).thenReturn(saved);

        // users
        User u1 = new User(); u1.setId(10L);
        User u2 = new User(); u2.setId(20L);
        when(userService.getUserById(10L)).thenReturn(Optional.of(u1));
        when(userService.getUserById(20L)).thenReturn(Optional.of(u2));

        Expense result = expenseService.updateSharedExpense(100, 40, req);
        assertNotNull(result);
        verify(expenseShareDAL).deleteSharesByExpenseId(40);
        verify(expenseShareDAL, atLeast(2)).saveShare(any(ExpenseShare.class));
    }

    @Test
    void updateSharedExpense_ShouldThrow_WhenExpenseNotBelongToTrip() {
        SharedExpenseRequest req = new SharedExpenseRequest();
        req.setDescription("Update shared");
        req.setAmount(new BigDecimal("80.00"));
        req.setCategoryId(1);
        req.setCurrency("EUR");
        req.setPaidByParticipantId(5000);
        req.setSplitWithParticipantIds(Arrays.asList(5000));
        req.setSplitEvenly(true);

        Expense existing = new Expense();
        existing.setExpenseId(50); existing.setTripId(999); // different trip
        when(expenseDAL.getExpenseById(50)).thenReturn(Optional.of(existing));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> expenseService.updateSharedExpense(100, 50, req));
        assertEquals("Expense does not belong to this trip", ex.getMessage());
    }

    @Test
    void validation_ShouldRejectInvalidSharedExpenseRequest() {
        SharedExpenseRequest req = new SharedExpenseRequest();
        req.setDescription(" "); // blank
        req.setAmount(new BigDecimal("0"));
        req.setSplitWithParticipantIds(Collections.emptyList());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> expenseService.createSharedExpense(100, req));
        assertTrue(ex.getMessage().contains("Description is required")
                || ex.getMessage().contains("Amount must be greater than 0")
                || ex.getMessage().contains("Split participants are required"));
    }

    @Test
    void createSharedExpense_ShouldThrow_WhenCategoryNotFound() {
        SharedExpenseRequest req = new SharedExpenseRequest();
        req.setDescription("Dinner");
        req.setAmount(new BigDecimal("50.00"));
        req.setCategoryId(999);
        req.setCurrency("EUR");
        req.setPaidByParticipantId(1000);
        req.setSplitWithParticipantIds(Arrays.asList(1000));
        req.setSplitEvenly(true);
        when(expenseCategoryService.getCategoryById(999L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> expenseService.createSharedExpense(100, req));
        assertTrue(ex.getMessage().contains("Category not found"));
    }

    @Test
    void createSharedExpense_ShouldThrow_WhenPayerParticipantNotFound() {
        SharedExpenseRequest req = new SharedExpenseRequest();
        req.setDescription("Dinner");
        req.setAmount(new BigDecimal("50.00"));
        req.setCategoryId(1);
        req.setCurrency("EUR");
        req.setPaidByParticipantId(1000);
        req.setSplitWithParticipantIds(Arrays.asList(1000));
        req.setSplitEvenly(true);
        when(expenseCategoryService.getCategoryById(1L)).thenReturn(Optional.of(new ExpenseCategory()));
        when(tripParticipantService.getParticipantById(1000)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> expenseService.createSharedExpense(100, req));
        assertTrue(ex.getMessage().contains("Payer participant not found"));
    }

    @Test
    void createSharedExpense_ShouldThrow_WhenSplitParticipantNotFound() {
        SharedExpenseRequest req = new SharedExpenseRequest();
        req.setDescription("Dinner");
        req.setAmount(new BigDecimal("50.00"));
        req.setCategoryId(1);
        req.setCurrency("EUR");
        req.setPaidByParticipantId(1000);
        req.setSplitWithParticipantIds(Arrays.asList(1000, 1001));
        req.setSplitEvenly(true);
        when(expenseCategoryService.getCategoryById(1L)).thenReturn(Optional.of(new ExpenseCategory()));
        TripParticipant payer = new TripParticipant();
        payer.setId(1000); payer.setTripId(100); payer.setUserId(10L);
        when(tripParticipantService.getParticipantById(1000)).thenReturn(Optional.of(payer));
        when(tripParticipantService.getParticipantById(1001)).thenReturn(Optional.empty());
        Expense saved = new Expense();
        saved.setExpenseId(70);
        saved.setTripId(100);
        saved.setAmount(new BigDecimal("50.00"));
        saved.setPaidBy(10L);
        when(expenseDAL.saveExpense(any(Expense.class))).thenReturn(saved);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> expenseService.createSharedExpense(100, req));
        assertTrue(ex.getMessage().contains("Participant not found"));
    }

    @Test
    void createSharedExpense_ShouldThrow_WhenCustomSplitsDoNotSumToTotal() {
        SharedExpenseRequest req = new SharedExpenseRequest();
        req.setDescription("Taxi");
        req.setAmount(new BigDecimal("100.00"));
        req.setCategoryId(1);
        req.setCurrency("EUR");
        req.setPaidByParticipantId(2000);
        req.setSplitWithParticipantIds(Arrays.asList(2000, 2001));
        req.setSplitEvenly(false);
        Map<String, BigDecimal> custom = new HashMap<>();
        custom.put("2000", new BigDecimal("40.00"));
        custom.put("2001", new BigDecimal("50.00")); // total 90, not 100
        req.setCustomSplits(custom);
        when(expenseCategoryService.getCategoryById(1L)).thenReturn(Optional.of(new ExpenseCategory()));
        TripParticipant payer = new TripParticipant();
        payer.setId(2000); payer.setTripId(100); payer.setUserId(10L);
        TripParticipant other = new TripParticipant();
        other.setId(2001); other.setTripId(100); other.setUserId(20L);
        when(tripParticipantService.getParticipantById(2000)).thenReturn(Optional.of(payer));
        when(tripParticipantService.getParticipantById(2001)).thenReturn(Optional.of(other));
        User u1 = new User(); u1.setId(10L);
        User u2 = new User(); u2.setId(20L);
        when(userService.getUserById(10L)).thenReturn(Optional.of(u1));
        when(userService.getUserById(20L)).thenReturn(Optional.of(u2));
        Expense saved = new Expense();
        saved.setExpenseId(81);
        saved.setTripId(100);
        saved.setAmount(new BigDecimal("100.00"));
        saved.setPaidBy(10L);
        when(expenseDAL.saveExpense(any(Expense.class))).thenReturn(saved);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> expenseService.createSharedExpense(100, req));
        assertTrue(ex.getMessage().contains("do not add up to total expense amount"));
    }
}
