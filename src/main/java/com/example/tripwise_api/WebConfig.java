package com.example.tripwise_api;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {
    // CORS configuration is handled in SecurityConfig to avoid conflicts
    // Removing duplicate CORS configuration from here
}
