package com.example.tripwise_api.Service;

import com.example.tripwise_api.Model.ExpenseCategory;
import com.example.tripwise_api.Repository.ExpenseCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenseCategoryService {

    @Autowired
    private ExpenseCategoryRepository categoryRepository;

    public List<ExpenseCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<ExpenseCategory> getActiveCategories() {
        return categoryRepository.findByIsActiveTrue();
    }

    public Optional<ExpenseCategory> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public ExpenseCategory createCategory(ExpenseCategory category) {
        if (categoryRepository.existsByCategoryName(category.getCategoryName())) {
            throw new IllegalArgumentException("Category with name '" + category.getCategoryName() + "' already exists");
        }
        return categoryRepository.save(category);
    }

    public ExpenseCategory updateCategory(Long id, ExpenseCategory categoryDetails) {
        ExpenseCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));

        category.setCategoryName(categoryDetails.getCategoryName());
        category.setCategoryDescription(categoryDetails.getCategoryDescription());
        category.setIsActive(categoryDetails.getIsActive());

        return categoryRepository.save(category);
    }

    public void deactivateCategory(Long id) {
        ExpenseCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));
        category.setIsActive(false);
        categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
