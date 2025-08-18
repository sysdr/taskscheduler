package com.taskscheduler.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class DatabaseConfig {
    
    @Value("${spring.profiles.active:dev}")
    private String activeProfile;
    
    @Bean
    @Profile("prod")
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            // In production, run all migrations including PostgreSQL-specific ones
            flyway.migrate();
        };
    }
    
    @Bean
    @Profile("dev")
    public FlywayMigrationStrategy devFlywayMigrationStrategy() {
        return flyway -> {
            // In development, only run H2-compatible migrations
            flyway.migrate();
        };
    }
}


