package com.project.ExternalApi.exception;

import org.springframework.http.HttpStatus;

public class WeatherServiceUnavailableException extends WeatherApiException {

    public WeatherServiceUnavailableException(String message) {
        super("Weather service unavailable: " + message, HttpStatus.SERVICE_UNAVAILABLE);
    }
}