package com.example.tripwise_api.config;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


/**
 * Flyway configuration for production environment
 * Ensures Flyway uses the correct JDBC URL format
 */
@Configuration
@Profile("prod")
public class FlywayConfig {

    private static final Logger logger = LoggerFactory.getLogger(FlywayConfig.class);

    @Value("${SPRING_DATASOURCE_URL:}")
    private String datasourceUrl;

    @Value("${SPRING_DATASOURCE_USERNAME:}")
    private String username;

    @Value("${SPRING_DATASOURCE_PASSWORD:}")
    private String password;

    @Bean
    public Flyway flyway() {
        logger.info("Configuring Flyway with datasource URL: {}",
            datasourceUrl != null ? datasourceUrl.replaceAll("://[^@]+@", "://***:***@") : "null");

        // Convert postgres:// to jdbc:postgresql:// if needed
        String jdbcUrl = datasourceUrl;

        if (datasourceUrl != null && datasourceUrl.startsWith("postgres://")) {
            jdbcUrl = datasourceUrl.replace("postgres://", "jdbc:postgresql://");
            logger.info("Converted postgres:// URL to jdbc:postgresql:// format");
        } else if (datasourceUrl != null && !datasourceUrl.startsWith("jdbc:")) {
            jdbcUrl = "jdbc:" + datasourceUrl;
            logger.info("Added jdbc: prefix to URL");
        }

        logger.info("Final Flyway JDBC URL: {}",
            jdbcUrl != null ? jdbcUrl.replaceAll("://[^@]+@", "://***:***@") : "null");

        return Flyway.configure()
                .dataSource(jdbcUrl, username, password)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .validateOnMigrate(false)
                .outOfOrder(true)
                .cleanDisabled(true)
                .createSchemas(true)
                .load();
    }

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            try {
                logger.info("Starting Flyway migration strategy");
                // Repair the Flyway schema history table if needed
                logger.info("Repairing Flyway schema history table");
                flyway.repair();
                // Run migrations
                logger.info("Running Flyway migrations");
                int migrationsApplied = flyway.migrate().migrationsExecuted;
                logger.info("Successfully applied {} migrations", migrationsApplied);
            } catch (Exception e) {
                logger.error("Flyway migration failed: ", e);
                throw new RuntimeException("Database migration failed", e);
            }
        };
    }
}

