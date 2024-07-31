package com.bankofapi.hackathon.embedded_chat_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class IndicativeRatesService {

    @Value("${citizenshipverification.token-url}")
    private String tokenUrl;

    @Value("${indicativerates.client-id}")
    private String clientId;

    @Value("${indicativerates.client-secret}")
    private String clientSecret;

    @Value("${indicativerates.api-url}")
    private String apiUrl;

    //fromCurrency=GBP&toCurrency=EUR&
    public record FxIndicativeRatesResponse(Data data) {}
    public record Data(BigDecimal amountCurrency, BigDecimal counterAmount, String fromCurrency, BigDecimal inverseRate,
                       BigDecimal indicativeRate, LocalDateTime timestamp, BigDecimal toCurrency) {}

    public FxIndicativeRatesResponse getFxIndicativeRateResponse(String fromCurrency, String toCurrency) {
        RestClient restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization", "Bearer " + getToken().accessToken())
                .build();
        return restClient.get()
                .uri("/trade-execution/fx/v1/quote/indicative?fromCurrency={fromCurrency}&toCurrency={toCurrency}", fromCurrency, toCurrency)
                .retrieve()
                .body(FxIndicativeRatesResponse.class);

    }

    public LoanQuotesService.TokenResponse getToken() {
        RestClient restClient = RestClient.builder()
                .baseUrl(tokenUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("scope", "indicativefxrate:search");
        LoanQuotesService.TokenResponse response = restClient.post()
                .uri("/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(LoanQuotesService.TokenResponse.class);
        System.out.println("Token response is " + response);
        return response;
    }

}
