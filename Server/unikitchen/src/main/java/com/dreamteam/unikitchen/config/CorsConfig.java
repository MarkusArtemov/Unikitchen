package com.dreamteam.unikitchen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    // Define a bean to configure CORS
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

            // Configure CORS mappings for the application
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("*") // Allow all origins
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow specified HTTP methods
                        .allowCredentials(false) // Do not allow cookies/credentials in CORS requests
                        .allowedHeaders("*") // Allow all headers in requests
                        .exposedHeaders("Authorization")
                        .maxAge(3600); // Cache CORS preflight responses for 1 hour
            }
        };
    }
}
