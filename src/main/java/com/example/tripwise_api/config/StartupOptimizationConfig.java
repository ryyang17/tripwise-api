package com.example.tripwise_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
@Profile("prod")
public class StartupOptimizationConfig {

    private final DataSource dataSource;

    public StartupOptimizationConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationStartup() {
        System.out.println("=== TripWise API Starting Up ===");
        System.out.println("Time: " + new java.util.Date());

        // Warm up database connection pool
        try (Connection connection = dataSource.getConnection()) {
            boolean valid = connection.isValid(10);
            System.out.println("Database connection test: " + (valid ? "SUCCESS" : "FAILED"));
            System.out.println("Database URL: " + connection.getMetaData().getURL());
        } catch (SQLException e) {
            System.err.println("Database connection test FAILED: " + e.getMessage());
        }

        // Print memory information
        Runtime runtime = Runtime.getRuntime();
        System.out.println("JVM Memory - Max: " + (runtime.maxMemory() / 1024 / 1024) + "MB, " +
                          "Total: " + (runtime.totalMemory() / 1024 / 1024) + "MB, " +
                          "Free: " + (runtime.freeMemory() / 1024 / 1024) + "MB");

        System.out.println("=== TripWise API Ready ===");
    }
}
