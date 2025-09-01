package com.taskscheduler.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.taskscheduler.repository")
@EnableTransactionManagement
public class DatabaseConfig {
    // Configuration is handled through application.yml
}
