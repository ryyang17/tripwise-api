package com.example.tripwise_api.dto.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateCategoryRequestTest {
    private UpdateCategoryRequest request;

    @BeforeEach
    void setUp() {
        request = new UpdateCategoryRequest();
    }

    @Test
    void canInstantiate() {
        UpdateCategoryRequest req = new UpdateCategoryRequest();
        assertNotNull(req);
    }

    @Test
    void setAndGetCategoryName_ShouldWorkCorrectly() {
        request.setCategoryName("Food");
        assertEquals("Food", request.getCategoryName());
    }

    @Test
    void setAndGetCategoryDescription_ShouldWorkCorrectly() {
        request.setCategoryDescription("Meals and dining");
        assertEquals("Meals and dining", request.getCategoryDescription());
    }

    @Test
    void setAndGetIsActive_ShouldWorkCorrectly() {
        request.setIsActive(true);
        assertTrue(request.getIsActive());
        request.setIsActive(false);
        assertFalse(request.getIsActive());
    }

    @Test
    void nullFields_ShouldBeAllowed() {
        request.setCategoryName(null);
        request.setCategoryDescription(null);
        request.setIsActive(null);
        assertNull(request.getCategoryName());
        assertNull(request.getCategoryDescription());
        assertNull(request.getIsActive());
    }
}
