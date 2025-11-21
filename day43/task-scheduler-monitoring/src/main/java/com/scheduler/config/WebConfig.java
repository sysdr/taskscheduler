package com.scheduler.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Configuration
public class WebConfig {
    
    @RestController
    public static class FaviconController {
        @GetMapping("favicon.ico")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void favicon() {
            // Return 204 No Content to prevent 404 errors
        }
    }
}

