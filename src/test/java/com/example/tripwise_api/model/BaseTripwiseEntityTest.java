package com.example.tripwise_api.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class BaseTripwiseEntityTest {
    private static class TestEntity extends BaseTripwiseEntity {}
    private TestEntity entity;
    private User user;

    @BeforeEach
    void setUp() {
        entity = new TestEntity();
        user = new User();
        user.setId(42L);
        user.setUsername("baseuser");
    }

    @Test
    void setAndGetId_ShouldWorkCorrectly() {
        entity.setId(123L);
        assertEquals(123L, entity.getId());
    }

    @Test
    void setAndGetUser_ShouldWorkCorrectly() {
        entity.setUser(user);
        assertEquals(user, entity.getUser());
        assertEquals(42L, entity.getUser().getId());
    }

    @Test
    void setAndGetTripId_ShouldWorkCorrectly() {
        entity.setTripId(99L);
        assertEquals(99L, entity.getTripId());
    }

    @Test
    void setAndGetProvider_ShouldWorkCorrectly() {
        entity.setProvider("Test Provider");
        assertEquals("Test Provider", entity.getProvider());
    }

    @Test
    void setAndGetRawPayload_ShouldWorkCorrectly() {
        entity.setRawPayload("{\"foo\":\"bar\"}");
        assertEquals("{\"foo\":\"bar\"}", entity.getRawPayload());
    }

    @Test
    void setAndGetCreatedDate_ShouldWorkCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedDate(now);
        assertEquals(now, entity.getCreatedDate());
    }

    @Test
    void createdDate_ShouldBeInitializedByDefault() {
        TestEntity newEntity = new TestEntity();
        assertNotNull(newEntity.getCreatedDate());
        assertTrue(newEntity.getCreatedDate().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}

