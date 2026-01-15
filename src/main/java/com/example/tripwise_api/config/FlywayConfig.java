package com.example.tripwise_api.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

/**
 * Flyway configuration for production environment
 * Ensures Flyway uses the correct JDBC URL format
 */
@Configuration
@Profile("prod")
public class FlywayConfig {

    @Value("${SPRING_DATASOURCE_URL:}")
    private String datasourceUrl;

    @Value("${SPRING_DATASOURCE_USERNAME:}")
    private String username;

    @Value("${SPRING_DATASOURCE_PASSWORD:}")
    private String password;

    @Bean
    public Flyway flyway() {
        // Convert postgres:// to jdbc:postgresql:// if needed
        String jdbcUrl = datasourceUrl;

        if (datasourceUrl != null && datasourceUrl.startsWith("postgres://")) {
            jdbcUrl = datasourceUrl.replace("postgres://", "jdbc:postgresql://");
        } else if (datasourceUrl != null && !datasourceUrl.startsWith("jdbc:")) {
            jdbcUrl = "jdbc:" + datasourceUrl;
        }

        return Flyway.configure()
                .dataSource(jdbcUrl, username, password)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .validateOnMigrate(false)
                .outOfOrder(true)
                .cleanDisabled(true)
                .load();
    }

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            // Repair the Flyway schema history table if needed
            flyway.repair();
            // Run migrations
            flyway.migrate();
        };
    }
}

