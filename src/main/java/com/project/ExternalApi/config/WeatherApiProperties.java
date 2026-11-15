package com.project.ExternalApi.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "weather.api")
public class WeatherApiProperties {

    // Getters and setters
    private String key;
    private String baseUrl;
    private String unitGroup;

    public void setKey(String key) {
        this.key = key;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setUnitGroup(String unitGroup) {
        this.unitGroup = unitGroup;
    }
}
