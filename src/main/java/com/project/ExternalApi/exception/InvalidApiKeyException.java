package com.project.ExternalApi.exception;

import org.springframework.http.HttpStatus;

public class InvalidApiKeyException extends WeatherApiException {

    public InvalidApiKeyException() {
        super("Invalid or missing API key", HttpStatus.UNAUTHORIZED);
    }
}