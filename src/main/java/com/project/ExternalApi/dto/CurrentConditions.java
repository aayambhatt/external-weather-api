package com.project.ExternalApi.dto;

public record CurrentConditions(
    String datetime,
    double temp,
    String conditions,
    double windspeed,
    double humidity
) {}