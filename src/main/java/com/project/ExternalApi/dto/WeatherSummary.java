package com.project.ExternalApi.dto;

import java.util.List;

public record WeatherSummary(
    String city,
    double currentTemp,
    double todayHigh,
    double todayLow,
    String conditions,
    String description,
    List<DailySummary> forecast
) {}