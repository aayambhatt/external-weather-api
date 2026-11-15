package com.project.ExternalApi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class RestApiController {

    @GetMapping("/hello")
    public String hello(){
        return "Hello World";
    }

    @GetMapping(value = "/testWeatherApi")
    public String callApi(){
        final String uri = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/London,UK?unitGroup=metric";
        RestTemplate restTemplate = new RestTemplate();

        String result = restTemplate.getForObject(uri, String.class);

       return result;
    }

}
