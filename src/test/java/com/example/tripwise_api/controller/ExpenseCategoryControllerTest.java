package com.example.tripwise_api.controller;

import com.example.tripwise_api.model.ExpenseCategory;
import com.example.tripwise_api.service.ExpenseCategoryService;
import com.example.tripwise_api.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExpenseCategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class ExpenseCategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExpenseCategoryService categoryService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void getAllCategories_returnsList() throws Exception {
        ExpenseCategory first = buildCategory(1L, "Travel", "Travel expenses", true);
        ExpenseCategory second = buildCategory(2L, "Food", "Food expenses", true);
        when(categoryService.getAllCategories()).thenReturn(List.of(first, second));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].categoryId").value(1))
                .andExpect(jsonPath("$[1].categoryName").value("Food"));
    }

    @Test
    void getActiveCategories_returnsList() throws Exception {
        ExpenseCategory active = buildCategory(3L, "Active", "Active only", true);
        when(categoryService.getActiveCategories()).thenReturn(List.of(active));

        mockMvc.perform(get("/api/categories/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].isActive").value(true));
    }

    @Test
    void getCategoryById_returnsCategory() throws Exception {
        ExpenseCategory category = buildCategory(5L, "Misc", "Miscellaneous", true);
        when(categoryService.getCategoryById(5L)).thenReturn(Optional.of(category));

        mockMvc.perform(get("/api/categories/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryName").value("Misc"));
    }

    @Test
    void getCategoryById_whenMissing_returnsNotFound() throws Exception {
        when(categoryService.getCategoryById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/categories/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createCategory_returnsCreated() throws Exception {
        ExpenseCategory request = buildCategory(null, "New", "New entry", true);
        ExpenseCategory saved = buildCategory(11L, "New", "New entry", true);
        when(categoryService.createCategory(any(ExpenseCategory.class))).thenReturn(saved);

        mockMvc.perform(post("/api/categories")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryId").value(11));
    }

    @Test
    void createCategory_whenInvalid_returnsBadRequest() throws Exception {
        when(categoryService.createCategory(any(ExpenseCategory.class)))
                .thenThrow(new IllegalArgumentException("Already exists"));

        mockMvc.perform(post("/api/categories")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(buildCategory(null, "New", "", true))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCategory_returnsUpdated() throws Exception {
        ExpenseCategory updateRequest = buildCategory(null, "Updated", "Updated desc", false);
        ExpenseCategory updated = buildCategory(2L, "Updated", "Updated desc", false);
        when(categoryService.updateCategory(eq(2L), any(ExpenseCategory.class))).thenReturn(updated);

        mockMvc.perform(put("/api/categories/2")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(false));
    }

    @Test
    void updateCategory_whenMissing_returnsNotFound() throws Exception {
        when(categoryService.updateCategory(anyLong(), any(ExpenseCategory.class)))
                .thenThrow(new IllegalArgumentException("Not found"));

        mockMvc.perform(put("/api/categories/20")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(buildCategory(null, "Whatever", "", true))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCategory_returnsOk() throws Exception {
        doNothing().when(categoryService).deactivateCategory(3L);

        mockMvc.perform(delete("/api/categories/3"))
                .andExpect(status().isOk())
                .andExpect(content().string("Category deactivated successfully"));
    }

    @Test
    void deleteCategory_whenMissing_returnsNotFound() throws Exception {
        doThrow(new IllegalArgumentException("Not found"))
                .when(categoryService).deactivateCategory(7L);

        mockMvc.perform(delete("/api/categories/7"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Not found"));
    }

    @Test
    void hardDeleteCategory_returnsOk() throws Exception {
        doNothing().when(categoryService).deleteCategory(4L);

        mockMvc.perform(delete("/api/categories/4/hard"))
                .andExpect(status().isOk())
                .andExpect(content().string("Category permanently deleted"));
    }

    @Test
    void hardDeleteCategory_whenMissing_returnsNotFound() throws Exception {
        doThrow(new IllegalArgumentException("Gone"))
                .when(categoryService).deleteCategory(10L);

        mockMvc.perform(delete("/api/categories/10/hard"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Gone"));
    }

    private ExpenseCategory buildCategory(Long id, String name, String description, Boolean active) {
        ExpenseCategory category = new ExpenseCategory();
        category.setCategoryId(id);
        category.setCategoryName(name);
        category.setCategoryDescription(description);
        category.setIsActive(active);
        return category;
    }
}
