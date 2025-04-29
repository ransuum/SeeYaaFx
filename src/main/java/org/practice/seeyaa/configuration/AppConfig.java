package org.practice.seeyaa.configuration;

import javafx.stage.Stage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AppConfig {
    @Bean
    public Map<String, Stage> openStages() {
        return new HashMap<>();
    }
}
