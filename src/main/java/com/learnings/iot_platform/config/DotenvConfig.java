package com.learnings.iot_platform.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PostMapping;

@Configuration
public class DotenvConfig {
    @PostMapping
    public void initializeDotenv() {
        Dotenv.configure()
                .directory("./")
                .load()
                .entries()
                .forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }
}
