package com.taskscheduler.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String webStaticPath = Paths.get("web/static").toAbsolutePath().toString();
        registry.addResourceHandler("/web/static/**")
                .addResourceLocations("file:" + webStaticPath + "/");
    }
}
