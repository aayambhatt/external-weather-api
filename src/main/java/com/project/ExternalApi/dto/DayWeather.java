package com.project.ExternalApi.dto;

import java.util.List;

public record DayWeather(
    String datetime,
    double tempmax,
    double tempmin,
    double temp,
    String conditions,
    String description,
    List<HourWeather> hours
) {}