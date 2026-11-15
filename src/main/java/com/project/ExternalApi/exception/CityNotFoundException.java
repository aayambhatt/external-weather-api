package com.project.ExternalApi.exception;

import org.springframework.http.HttpStatus;

public class CityNotFoundException extends WeatherApiException {

    public CityNotFoundException(String city) {
        super("City not found: " + city, HttpStatus.NOT_FOUND);
    }
}