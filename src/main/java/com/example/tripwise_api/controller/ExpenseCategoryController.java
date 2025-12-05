package com.example.tripwise_api.controller;

import com.example.tripwise_api.model.ExpenseCategory;
import com.example.tripwise_api.service.ExpenseCategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(originPatterns = "*")
public class ExpenseCategoryController {

    private final ExpenseCategoryService categoryService;

    public ExpenseCategoryController(ExpenseCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // GET /api/categories - All categories
    @GetMapping
    public ResponseEntity<List<ExpenseCategory>> getAllCategories() {
        List<ExpenseCategory> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // GET /api/categories/active - Only active categories
    @GetMapping("/active")
    public ResponseEntity<List<ExpenseCategory>> getActiveCategories() {
        List<ExpenseCategory> categories = categoryService.getActiveCategories();
        return ResponseEntity.ok(categories);
    }

    // GET /api/categories/{id} - Specific category
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseCategory> getCategoryById(@PathVariable Long id) {
        Optional<ExpenseCategory> category = categoryService.getCategoryById(id);
        return category.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/categories - Create new category
    @PostMapping
    public ResponseEntity<ExpenseCategory> createCategory(@RequestBody ExpenseCategory category) {
        try {
            ExpenseCategory savedCategory = categoryService.createCategory(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT /api/categories/{id} - Update category
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseCategory> updateCategory(@PathVariable Long id, @RequestBody ExpenseCategory categoryDetails) {
        try {
            ExpenseCategory updatedCategory = categoryService.updateCategory(id, categoryDetails);
            return ResponseEntity.ok(updatedCategory);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // DELETE /api/categories/{id} - Soft delete (deactivate)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deactivateCategory(id);
            return ResponseEntity.ok("Category deactivated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // DELETE /api/categories/{id}/hard - Permanent delete
    @DeleteMapping("/{id}/hard")
    public ResponseEntity<String> hardDeleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok("Category permanently deleted");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}