package com.example.tripwise_api.controller;


import com.example.tripwise_api.Model.ExpenseCategory;
import com.example.tripwise_api.Repository.ExpenseCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class ExpenseCategoryController {

    @Autowired
    private ExpenseCategoryRepository categoryRepository;

    // GET /api/categories - Alle categorieën
    @GetMapping
    public List<ExpenseCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    // GET /api/categories/active - Alleen actieve categorieën
    @GetMapping("/active")
    public List<ExpenseCategory> getActiveCategories() {
        return categoryRepository.findByIsActiveTrue();
    }

    // GET /api/categories/{id} - Specifieke categorie
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseCategory> getCategoryById(@PathVariable Long id) {
        Optional<ExpenseCategory> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            return ResponseEntity.ok(category.get());
        }
        return ResponseEntity.notFound().build();
    }

    // POST /api/categories - Nieuwe categorie
    @PostMapping
    public ResponseEntity<ExpenseCategory> createCategory(@RequestBody ExpenseCategory category) {
        // Check of naam al bestaat
        if (categoryRepository.existsByCategoryName(category.getCategoryName())) {
            return ResponseEntity.badRequest().build();
        }

        ExpenseCategory savedCategory = categoryRepository.save(category);
        return ResponseEntity.ok(savedCategory);
    }

    // PUT /api/categories/{id} - Update categorie
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseCategory> updateCategory(@PathVariable Long id, @RequestBody ExpenseCategory categoryDetails) {
        Optional<ExpenseCategory> optionalCategory = categoryRepository.findById(id);

        if (optionalCategory.isPresent()) {
            ExpenseCategory category = optionalCategory.get();

            // Update velden
            category.setCategoryName(categoryDetails.getCategoryName());
            category.setCategoryDescription(categoryDetails.getCategoryDescription());
            category.setIsActive(categoryDetails.getIsActive());

            ExpenseCategory updatedCategory = categoryRepository.save(category);
            return ResponseEntity.ok(updatedCategory);
        }

        return ResponseEntity.notFound().build();
    }

    // DELETE /api/categories/{id} - Verwijder categorie (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        Optional<ExpenseCategory> optionalCategory = categoryRepository.findById(id);

        if (optionalCategory.isPresent()) {
            ExpenseCategory category = optionalCategory.get();

            // Soft delete - zet alleen is_active op false
            category.setIsActive(false);
            categoryRepository.save(category);

            return ResponseEntity.ok("Category deactivated successfully");
        }

        return ResponseEntity.notFound().build();
    }

    // DELETE /api/categories/{id}/hard - Echt verwijderen
    @DeleteMapping("/{id}/hard")
    public ResponseEntity<String> hardDeleteCategory(@PathVariable Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return ResponseEntity.ok("Category permanently deleted");
        }
        return ResponseEntity.notFound().build();
    }
}