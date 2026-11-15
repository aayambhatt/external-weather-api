package com.project.ExternalApi.exception;

import org.springframework.http.HttpStatus;

public class WeatherApiException extends RuntimeException {

    private final HttpStatus status;

    public WeatherApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}