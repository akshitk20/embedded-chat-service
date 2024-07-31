package com.bankofapi.hackathon.embedded_chat_service.service.bankofapi;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class CitizenshipVerification implements Function<CitizenshipVerification.Request, CitizenshipVerification.Response> {


    @Value("${citizenshipverification.api-url}")
    private String apiUrl;


    @Value("${citizenshipverification.token-url}")
    private String tokenUrl;

    @Override
    public Response apply(Request request) {
        System.out.println("Citizenship Verificaiton Request " + request);
        RestClient restClient = RestClient.builder()
                .baseUrl(tokenUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
        TokenResponse response = restClient.post()
                .uri("/token")
                .retrieve()
                .body(TokenResponse.class);
        System.out.println("Citizenship Verification get token Response" + response);
        restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + response.accessToken())
                .build();
        Response citizenshipVerificationResponse = restClient.post()
                .uri("/zerocode/bankofapis.com/customer-citizenship/v3/attributes/citizenship")
                .retrieve()
                .body(Response.class);
        return citizenshipVerificationResponse;
    }

    public record Request(String city) {}
    public record Response(String response) {}
    public record TokenResponse(String accessToken,  String tokenType, Integer expiresIn) {}
    public record Location(String name, String region, String country, Long lat, Long lon){}
    public record Current(String temp_f, Condition condition, String wind_mph, String humidity) {}
    public record Condition(String text){}
}
