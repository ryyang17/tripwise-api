package com.example.tripwise_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/detailed")
    public ResponseEntity<Map<String, Object>> detailedHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("timestamp", LocalDateTime.now());
        health.put("status", "UP");

        // Test database connection
        try (Connection connection = dataSource.getConnection()) {
            long startTime = System.currentTimeMillis();
            boolean valid = connection.isValid(5);
            long endTime = System.currentTimeMillis();

            health.put("database", Map.of(
                "status", valid ? "UP" : "DOWN",
                "responseTime", (endTime - startTime) + "ms",
                "url", connection.getMetaData().getURL()
            ));
        } catch (SQLException e) {
            health.put("database", Map.of(
                "status", "DOWN",
                "error", e.getMessage()
            ));
        }

        // JVM info
        Runtime runtime = Runtime.getRuntime();
        health.put("jvm", Map.of(
            "maxMemory", runtime.maxMemory() / 1024 / 1024 + "MB",
            "totalMemory", runtime.totalMemory() / 1024 / 1024 + "MB",
            "freeMemory", runtime.freeMemory() / 1024 / 1024 + "MB",
            "usedMemory", (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024 + "MB"
        ));

        return ResponseEntity.ok(health);
    }

    @GetMapping("/ping")
    public ResponseEntity<Map<String, Object>> ping() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "pong");
        response.put("uptime", System.currentTimeMillis() - startTime);
        return ResponseEntity.ok(response);
    }

    private static final long startTime = System.currentTimeMillis();
}
