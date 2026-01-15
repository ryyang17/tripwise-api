package com.example.tripwise_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

/**
 * DataSource configuration for production environment
 * Handles automatic conversion of Render's PostgreSQL connection strings
 */
@Configuration
@Profile("prod")
public class DataSourceConfig {

    @Value("${SPRING_DATASOURCE_URL:}")
    private String datasourceUrl;

    @Value("${SPRING_DATASOURCE_USERNAME:}")
    private String username;

    @Value("${SPRING_DATASOURCE_PASSWORD:}")
    private String password;

    @Bean
    @Primary
    public DataSource dataSource() {
        // Render provides connection strings in format: postgres://...
        // Spring requires: jdbc:postgresql://...
        String jdbcUrl = datasourceUrl;

        if (datasourceUrl != null && datasourceUrl.startsWith("postgres://")) {
            jdbcUrl = datasourceUrl.replace("postgres://", "jdbc:postgresql://");
        } else if (datasourceUrl != null && !datasourceUrl.startsWith("jdbc:")) {
            jdbcUrl = "jdbc:" + datasourceUrl;
        }

        return DataSourceBuilder.create()
                .url(jdbcUrl)
                .username(username)
                .password(password)
                .driverClassName("org.postgresql.Driver")
                .build();
    }
}

