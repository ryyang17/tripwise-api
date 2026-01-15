package com.example.tripwise_api.config;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;

/**
 * Ensures database initialization happens after application startup
 * This provides an additional safety net for Flyway migrations
 */
@Configuration
@Profile("prod")
public class DatabaseInitializationConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializationConfig.class);

    @Autowired(required = false)
    private Flyway flyway;

    @EventListener(ApplicationReadyEvent.class)
    public void ensureDatabaseMigration() {
        if (flyway != null) {
            try {
                logger.info("Ensuring database migrations are complete...");
                var info = flyway.info();
                var pending = info.pending();

                if (pending.length > 0) {
                    logger.warn("Found {} pending migrations, executing them now", pending.length);
                    flyway.migrate();
                } else {
                    logger.info("All migrations are up to date");
                }

                // Log migration status
                var current = info.current();
                if (current != null) {
                    logger.info("Current migration version: {}", current.getVersion());
                } else {
                    logger.warn("No current migration version found - database may be empty");
                }

            } catch (Exception e) {
                logger.error("Failed to verify/execute database migrations: ", e);
                // Don't throw exception - let application start but log the error
            }
        } else {
            logger.warn("Flyway bean not found - migrations may not be configured properly");
        }
    }
}
