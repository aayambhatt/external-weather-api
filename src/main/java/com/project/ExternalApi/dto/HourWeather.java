package com.project.ExternalApi.dto;

public record HourWeather(
    String datetime,
    double temp,
    String conditions,
    double windspeed,
    double humidity
) {}