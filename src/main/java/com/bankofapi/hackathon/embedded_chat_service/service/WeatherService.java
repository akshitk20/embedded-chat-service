package com.bankofapi.hackathon.embedded_chat_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.function.Function;

@Service
public class WeatherService implements Function<WeatherService.Request, WeatherService.Response> {


    @Value("${weather.api-key}")
    private String apiKey;

    @Value("${weather.api-url}")
    private String apiUrl;

    public WeatherService() {
    }

    @Override
    public Response apply(Request request) {
        System.out.println("Weather Request " + request);
        RestClient restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
        Response response = restClient.get()
                .uri("/current.json?key={key}&q={q}", apiKey, request.city())
                .retrieve()
                .body(Response.class);
        System.out.println("Weather Response" + response);
        return response;
    }

    public record Request(String city) {}
    public record Response(Location location,Current current) {}
    public record Location(String name, String region, String country, Long lat, Long lon){}
    public record Current(String temp_f, Condition condition, String wind_mph, String humidity) {}
    public record Condition(String text){}
}
