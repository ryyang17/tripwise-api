package com.example.tripwise_api.controller;

import com.example.tripwise_api.dto.request.CreateExpenseRequest;
import com.example.tripwise_api.dto.request.SharedExpenseRequest;
import com.example.tripwise_api.dto.request.UpdateExpenseRequest;
import com.example.tripwise_api.model.Expense;
import com.example.tripwise_api.model.ExpenseCategory;
import com.example.tripwise_api.security.JwtAuthenticationFilter;
import com.example.tripwise_api.service.ExpenseCategoryService;
import com.example.tripwise_api.service.ExpenseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@WebMvcTest(controllers = ExpenseController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExpenseService expenseService;

    @MockBean
    private ExpenseCategoryService expenseCategoryService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setup() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getExpensesByTripId_returnsResult() throws Exception {
        Mockito.when(expenseService.getExpensesByTripId(anyInt())).thenReturn(Collections.singletonList(buildExpense(1, 1)));

        mockMvc.perform(get("/api/trips/1/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].expenseId").value(1));
    }

    @Test
    void getExpensesByTripId_withCurrency_returnsResult() throws Exception {
        Mockito.when(expenseService.getExpensesByTripIdWithCurrency(anyInt(), Mockito.eq("USD")))
                .thenReturn(Collections.singletonList(buildExpense(2, 1)));

        mockMvc.perform(get("/api/trips/1/expenses?currency=USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].expenseId").value(2));
    }

    @Test
    void getExpenseById_found() throws Exception {
        Mockito.when(expenseService.getExpenseById(1)).thenReturn(Optional.of(buildExpense(1, 1)));

        mockMvc.perform(get("/api/expenses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expenseId").value(1));
    }

    @Test
    void getExpenseById_notFound() throws Exception {
        Mockito.when(expenseService.getExpenseById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/expenses/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addExpense_valid() throws Exception {
        CreateExpenseRequest request = buildCreateExpenseRequest();
        ExpenseCategory category = new ExpenseCategory();
        category.setCategoryId(1L);
        Mockito.when(expenseCategoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        Expense savedExpense = buildExpense(1, 1);
        Mockito.when(expenseService.createExpense(eq(1), Mockito.any(Expense.class))).thenReturn(savedExpense);

        mockMvc.perform(post("/api/trips/1/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.expenseId").value(1));
    }

    @Test
    void addExpense_withTargetCurrency() throws Exception {
        CreateExpenseRequest request = buildCreateExpenseRequest();
        ExpenseCategory category = new ExpenseCategory();
        category.setCategoryId(1L);
        Mockito.when(expenseCategoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        Expense savedExpense = buildExpense(3, 1);
        Mockito.when(expenseService.createExpenseWithCurrency(eq(1), Mockito.any(Expense.class), eq("EUR")))
                .thenReturn(savedExpense);

        mockMvc.perform(post("/api/trips/1/expenses?targetCurrency=EUR")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.expenseId").value(3));
    }

    @Test
    void addExpense_missingCategoryId_returnsBadRequest() throws Exception {
        CreateExpenseRequest request = new CreateExpenseRequest();
        request.setTripId(1);
        request.setDescription("desc");
        request.setAmount(BigDecimal.valueOf(10));
        request.setCurrency("USD");
        request.setExpenseDate(LocalDate.now());

        mockMvc.perform(post("/api/trips/1/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addExpense_categoryMissing_returnsBadRequest() throws Exception {
        CreateExpenseRequest request = buildCreateExpenseRequest();
        Mockito.when(expenseCategoryService.getCategoryById(99L)).thenReturn(Optional.empty());

        request.setCategoryId(99L);
        mockMvc.perform(post("/api/trips/1/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateExpense_valid() throws Exception {
        UpdateExpenseRequest request = new UpdateExpenseRequest();
        request.setExpenseId(1);
        request.setDescription("updated");
        request.setAmount(BigDecimal.valueOf(20));
        request.setCurrency("USD");
        request.setExpenseDate(LocalDate.now());
        request.setCategoryId(1L);

        Expense existing = buildExpense(1, 1);
        Mockito.when(expenseService.getExpenseById(1)).thenReturn(Optional.of(existing));
        ExpenseCategory category = new ExpenseCategory();
        category.setCategoryId(1L);
        Mockito.when(expenseCategoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        Mockito.when(expenseService.updateExpense(eq(1), Mockito.any(Expense.class))).thenReturn(existing);

        mockMvc.perform(put("/api/expenses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expenseId").value(1));
    }

    @Test
    void updateExpense_notFound() throws Exception {
        UpdateExpenseRequest request = new UpdateExpenseRequest();
        request.setExpenseId(1);
        Mockito.when(expenseService.getExpenseById(1)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/expenses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateExpense_categoryMissing_returnsNotFound() throws Exception {
        UpdateExpenseRequest request = new UpdateExpenseRequest();
        request.setExpenseId(1);
        request.setCategoryId(99L);
        Expense existing = buildExpense(1, 1);
        Mockito.when(expenseService.getExpenseById(1)).thenReturn(Optional.of(existing));
        Mockito.when(expenseCategoryService.getCategoryById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/expenses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteExpense_success() throws Exception {
        mockMvc.perform(delete("/api/expenses/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteExpense_notFound() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("Expense not found")).when(expenseService).deleteExpense(99);

        mockMvc.perform(delete("/api/expenses/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Expense not found")));
    }

    @Test
    void addSharedExpense_valid() throws Exception {
        SharedExpenseRequest request = buildSharedExpenseRequest();
        Expense savedExpense = buildExpense(5, 1);
        Mockito.when(expenseService.createSharedExpense(eq(1), Mockito.any(SharedExpenseRequest.class))).thenReturn(savedExpense);

        mockMvc.perform(post("/api/trips/1/shared-expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.expenseId").value(5));
    }

    @Test
    void addSharedExpense_invalid() throws Exception {
        SharedExpenseRequest request = new SharedExpenseRequest();

        mockMvc.perform(post("/api/trips/1/shared-expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateSharedExpense_valid() throws Exception {
        SharedExpenseRequest request = buildSharedExpenseRequest();
        Expense savedExpense = buildExpense(6, 1);
        Mockito.when(expenseService.updateSharedExpense(eq(1), eq(1), Mockito.any(SharedExpenseRequest.class))).thenReturn(savedExpense);

        mockMvc.perform(put("/api/trips/1/shared-expenses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expenseId").value(6));
    }

    @Test
    void updateSharedExpense_notFound() throws Exception {
        SharedExpenseRequest request = buildSharedExpenseRequest(); // Use valid request so validation passes
        Mockito.when(expenseService.updateSharedExpense(eq(1), eq(99), Mockito.any(SharedExpenseRequest.class)))
                .thenThrow(new IllegalArgumentException("Expense not found"));

        mockMvc.perform(put("/api/trips/1/shared-expenses/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    private CreateExpenseRequest buildCreateExpenseRequest() {
        CreateExpenseRequest request = new CreateExpenseRequest();
        request.setTripId(1);
        request.setDescription("Desc");
        request.setAmount(BigDecimal.valueOf(15));
        request.setCurrency("USD");
        request.setExpenseDate(LocalDate.now());
        request.setCategoryId(1L);
        return request;
    }

    private SharedExpenseRequest buildSharedExpenseRequest() {
        SharedExpenseRequest request = new SharedExpenseRequest();
        request.setDescription("Shared");
        request.setAmount(BigDecimal.valueOf(50));
        request.setCurrency("USD");
        request.setCategoryId(1);
        request.setExpenseDate(LocalDate.now());
        request.setPaidByParticipantId(2);
        request.setSplitWithParticipantIds(Collections.singletonList(2));
        return request;
    }

    private Expense buildExpense(int expenseId, int tripId) {
        Expense expense = new Expense();
        expense.setExpenseId(expenseId);
        expense.setTripId(tripId);
        expense.setDescription("Desc");
        expense.setAmount(BigDecimal.valueOf(20));
        expense.setCurrency("USD");
        expense.setCategory(new ExpenseCategory());
        expense.setExpenseDate(LocalDate.now());
        return expense;
    }
}

