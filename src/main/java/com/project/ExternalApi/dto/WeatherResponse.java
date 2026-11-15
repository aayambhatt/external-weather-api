package com.project.ExternalApi.dto;

import java.util.List;

public record WeatherResponse(
    double queryCost,
    double latitude,
    double longitude,
    String resolvedAddress,
    String address,
    String timezone,
    double tzoffset,
    List<DayWeather> days,
    CurrentConditions currentConditions
) {}