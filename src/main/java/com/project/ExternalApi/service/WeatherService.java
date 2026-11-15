package com.project.ExternalApi.service;

import com.project.ExternalApi.config.WeatherApiProperties;
import com.project.ExternalApi.dto.WeatherResponse;
import org.springframework.web.client.RestClient;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;


@Service
public class WeatherService {

    private final RestClient restClient;
    private final WeatherApiProperties weatherApiProperties;

    public WeatherService(RestClient.Builder restClientBuilder, WeatherApiProperties weatherApiProperties) {
        this.weatherApiProperties = weatherApiProperties;
        this.restClient = restClientBuilder
                .baseUrl(weatherApiProperties.getBaseUrl())
                .build();
    }

    public WeatherResponse getWeather(String city) {
        String uri = UriComponentsBuilder.fromPath("/timeline/{city}")
                .queryParam("unitGroup", weatherApiProperties.getUnitGroup())
                .queryParam("key", weatherApiProperties.getKey())
                .buildAndExpand(city)
                .toUriString();

        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(WeatherResponse.class);
    }



}
