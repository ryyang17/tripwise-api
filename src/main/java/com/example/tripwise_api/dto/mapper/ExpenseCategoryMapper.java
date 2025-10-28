package com.example.tripwise_api.dto.mapper;

import com.example.tripwise_api.model.ExpenseCategory;
import com.example.tripwise_api.dto.request.CreateCategoryRequest;
import com.example.tripwise_api.dto.response.ExpenseCategoryResponse;
import java.time.LocalDateTime;

/**
 * Voorbeeld van een simpele mapper voor ExpenseCategory
 */
public class ExpenseCategoryMapper {

    /**
     * Van Database Entity -> Frontend Response
     */
    public static ExpenseCategoryResponse toResponse(ExpenseCategory entity) {
        // 1. Check voor null
        if (entity == null) return null;

        // 2. Maak nieuwe response
        ExpenseCategoryResponse response = new ExpenseCategoryResponse();

        // 3. Kopieer alle velden
        response.setCategoryId(entity.getCategoryId());
        response.setCategoryName(entity.getCategoryName());
        response.setCategoryDescription(entity.getCategoryDescription());
        response.setIsActive(entity.getIsActive());
        response.setCreatedDate(entity.getCreatedDate());

        // 4. Return het resultaat
        return response;
    }

    /**
     * Van Frontend Request -> Database Entity
     */
    public static ExpenseCategory toEntity(CreateCategoryRequest request) {
        // 1. Check voor null
        if (request == null) return null;

        // 2. Maak nieuwe entity
        ExpenseCategory entity = new ExpenseCategory();

        // 3. Kopieer velden van request
        entity.setCategoryName(request.getCategoryName());
        entity.setCategoryDescription(request.getCategoryDescription());
        entity.setIsActive(true); // default waarde
        entity.setCreatedDate(LocalDateTime.now()); // huidige tijd

        // 4. Return het resultaat
        return entity;
    }

    /**
     * Update bestaande entity met nieuwe waardes
     */
    public static void updateEntity(ExpenseCategory entity, CreateCategoryRequest request) {
        // 1. Veiligheidscheck
        if (entity == null || request == null) return;

        // 2. Update alleen wat we willen updaten
        if (request.getCategoryName() != null) {
            entity.setCategoryName(request.getCategoryName());
        }
        if (request.getCategoryDescription() != null) {
            entity.setCategoryDescription(request.getCategoryDescription());
        }
        // We updaten NIET createdDate - dat blijft hetzelfde
    }
}
