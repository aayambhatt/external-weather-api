package com.project.ExternalApi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.ExternalApi.dto.DailySummary;
import com.project.ExternalApi.dto.WeatherSummary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class WeatherService {

    private final RestClient restClient;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String unitGroup;

    public WeatherService(
            RestClient.Builder builder,
            @Value("${weather.api.base-url}") String baseUrl,
            @Value("${weather.api.key}") String apiKey,
            @Value("${weather.api.unit-group}") String unitGroup,
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper
    ) {
        this.restClient = builder.baseUrl(baseUrl).build();
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.unitGroup = unitGroup;
    }

    public WeatherSummary getWeather(String city) {
        // Normalize: trim + lowercase for cache key consistency
        String normalizedCity = city.trim().toLowerCase();
        String cacheKey = "weather:" + normalizedCity;

        // 1. CHECK CACHE
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return parseJson(cached);
        }

        // 2. CACHE MISS → Call API
        WeatherSummary summary = fetchAndParse(normalizedCity);

        // 3. STORE IN CACHE (12 hour TTL)
        redisTemplate.opsForValue().set(cacheKey, toJson(summary), Duration.ofHours(12));

        return summary;
    }

    private WeatherSummary fetchAndParse(String city) {
        //Building the URL
        String uri = UriComponentsBuilder.fromPath("/{city}")
                .queryParam("unitGroup", unitGroup)
                .queryParam("key", apiKey)
                .buildAndExpand(city)
                .toUriString();

        // Making the HTTP call
        Map<String, Object> res = restClient.get().uri(uri).retrieve().body(Map.class);

        // parse the response:

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

    private String toJson(WeatherSummary summary) {
        try {
            return objectMapper.writeValueAsString(summary);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize weather summary", e);
        }
    }

    private WeatherSummary parseJson(String json) {
        try {
            return objectMapper.readValue(json, WeatherSummary.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse cached weather", e);
        }
    }
}
