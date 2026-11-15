package com.project.ExternalApi.service;

import com.project.ExternalApi.config.WeatherApiProperties;
import com.project.ExternalApi.dto.DailySummary;
import com.project.ExternalApi.dto.WeatherResponse;
import com.project.ExternalApi.dto.WeatherSummary;
import com.project.ExternalApi.exception.CityNotFoundException;
import com.project.ExternalApi.exception.InvalidApiKeyException;
import com.project.ExternalApi.exception.WeatherApiException;
import com.project.ExternalApi.exception.WeatherServiceUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;


@Service
public class WeatherService {

    private final RestClient restClient;
    private final WeatherApiProperties weatherApiProperties;

    public WeatherService(RestClient.Builder restClientBuilder, WeatherApiProperties weatherApiProperties) {
        this.weatherApiProperties = weatherApiProperties;
        this.restClient = restClientBuilder
                .baseUrl(weatherApiProperties.getBaseUrl())
                .build();
    }

    public WeatherResponse getWeather(String city) {
        String uri = UriComponentsBuilder.fromPath("/{city}")
                .queryParam("unitGroup", weatherApiProperties.getUnitGroup())
                .queryParam("key", weatherApiProperties.getKey())
                .buildAndExpand(city)
                .toUriString();

        try {
            return restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(WeatherResponse.class);
        } catch (HttpClientErrorException e) {
            // 4xx errors from the API
            return handleClientError(e, city);
        } catch (HttpServerErrorException e) {
            // 5xx errors from the API
            throw new WeatherServiceUnavailableException(
                    "API returned " + e.getStatusCode() + ": " + e.getResponseBodyAsString()
            );
        } catch (ResourceAccessException e) {
            // Network/timeout errors
            throw new WeatherServiceUnavailableException(
                    "Unable to reach weather service: " + e.getMessage()
            );
        }
    }

    /**
     * Convert 4xx HTTP errors to our custom exceptions.
     * Visual Crossing returns 400 for both "city not found" and "invalid API key"
     * so we inspect the response body to differentiate.
     */
    private WeatherResponse handleClientError(HttpClientErrorException e, String city) {
        String body = e.getResponseBodyAsString();

        if (e.getStatusCode().value() == 401 || body.contains("API key")) {
            throw new InvalidApiKeyException();
        }

        if (e.getStatusCode().value() == 400 && body.contains("cannot be parsed")) {
            throw new CityNotFoundException(city);
        }

        // Generic 4xx fallback
        throw new WeatherApiException(
                "API error: " + e.getStatusCode() + " - " + body,
                (HttpStatus) e.getStatusCode()
        );
    }

/**
     * Public API: Get clean, trimmed weather summary for a city
     */
    public WeatherSummary getWeatherSummary(String city) {
        WeatherResponse fullResponse = getWeather(city);
        return mapToSummary(fullResponse, city);
    }

    /**
     * Maps the verbose 3rd-party response to our clean API contract.
     * This is the "Anti-Corruption Layer" pattern - we isolate our API
     * from the external API's schema changes.
     */
    private WeatherSummary mapToSummary(WeatherResponse response, String city) {
        // Current conditions (right now)
        var current = response.currentConditions();

        // Today's forecast (first day in the array)
        var today = response.days().get(0);

        // Build 3-day forecast summary (today + next 2 days)
        List<DailySummary> forecast = response.days().stream()
                .limit(3)
                .map(day -> new DailySummary(
                        day.datetime(),
                        day.tempmax(),
                        day.tempmin(),
                        day.conditions()
                ))
                .toList();

        return new WeatherSummary(
                city,
                current.temp(),
                today.tempmax(),
                today.tempmin(),
                current.conditions(),
                today.description(),
                forecast
        );
    }


}
