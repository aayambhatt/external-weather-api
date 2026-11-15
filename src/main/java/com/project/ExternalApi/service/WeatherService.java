package com.project.ExternalApi.service;

import com.project.ExternalApi.dto.DailySummary;
import com.project.ExternalApi.dto.WeatherSummary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Service
public class WeatherService {

    private final RestClient restClient;

    @Value("${weather.api.key}") private String apiKey;
    @Value("${weather.api.unit-group}") private String unitGroup;

    public WeatherService(RestClient.Builder builder, @Value("${weather.api.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public WeatherSummary getWeather(String city) {
        String uri = UriComponentsBuilder.fromPath("/{city}")
                .queryParam("unitGroup", unitGroup)
                .queryParam("key", apiKey)
                .buildAndExpand(city)
                .toUriString();

        Map<String, Object> res = restClient.get().uri(uri).retrieve().body(Map.class);

        var current = (Map<String, Object>) res.get("currentConditions");
        var days = (List<Map<String, Object>>) res.get("days");
        var today = days.get(0);

        var forecast = days.stream().limit(3).map(day -> new DailySummary(
                (String) day.get("datetime"),
                ((Number) day.get("tempmax")).doubleValue(),
                ((Number) day.get("tempmin")).doubleValue(),
                (String) day.get("conditions")
        )).toList();

        return new WeatherSummary(
                city,
                ((Number) current.get("temp")).doubleValue(),
                ((Number) today.get("tempmax")).doubleValue(),
                ((Number) today.get("tempmin")).doubleValue(),
                (String) current.get("conditions"),
                (String) today.get("description"),
                forecast
        );
    }
}
