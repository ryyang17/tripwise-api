package com.example.tripwise_api.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FlywayConfigTest {
    @Test
    void contextLoads() {
        FlywayConfig config = new FlywayConfig();
        assertNotNull(config);
    }
}

