package com.example.tripwise_api.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseInitializationConfigTest {
    @Test
    void canInstantiate() {
        DatabaseInitializationConfig config = new DatabaseInitializationConfig();
        assertNotNull(config);
    }
}
