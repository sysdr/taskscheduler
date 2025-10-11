package com.scheduler.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableRetry
public class RetryConfig {
    // Spring Retry configuration is handled via annotations
    // Additional custom retry templates can be defined here if needed
}
