package com.project.ExternalApi.dto;

public record DailySummary(
    String date,
    double high,
    double low,
    String conditions
) {}