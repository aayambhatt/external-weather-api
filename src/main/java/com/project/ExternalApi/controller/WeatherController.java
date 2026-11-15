package com.project.ExternalApi.controller;

import com.project.ExternalApi.dto.WeatherSummary;
import com.project.ExternalApi.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/{city}")
    public ResponseEntity<WeatherSummary> getWeather(@PathVariable String city) {
        if (city == null || city.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        WeatherSummary response = weatherService.getWeatherSummary(city.trim());
        return ResponseEntity.ok(response);
    }
}
