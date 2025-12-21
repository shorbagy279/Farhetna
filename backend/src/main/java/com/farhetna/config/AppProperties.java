package com.farhetna.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private BookingConfig booking = new BookingConfig();
    private StorageConfig storage = new StorageConfig();
    
    @Data
    public static class BookingConfig {
        private int codeValidityHours = 24;
        private int maxModifications = 3;
        private int codeExtensionHours = 2;
        private int maxTotalValidityHours = 36;
    }
    
    @Data
    public static class StorageConfig {
        private String uploadDir = "./uploads";
        private String imageDir = "./uploads/images";
        private String videoDir = "./uploads/videos";
    }
}

