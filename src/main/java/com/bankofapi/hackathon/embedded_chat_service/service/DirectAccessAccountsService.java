package com.bankofapi.hackathon.embedded_chat_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class DirectAccessAccountsService implements Function<DirectAccessAccountsService.DirectAccessAccountRequest, DirectAccessAccountsService.DirectAccessAccountResponse> {


    @Value("${citizenshipverification.api-url}")
    private String apiUrl;


    @Value("${citizenshipverification.token-url}")
    private String tokenUrl;

    @Value("${citizenshipverification.client-id}")
    private String clientId;

    @Value("${citizenshipverification.client-secret}")
    private String clientSecret;

    @Override
    public DirectAccessAccountResponse apply(DirectAccessAccountRequest request) {

        return getDirectAccessResponse(request);
    }

    public DirectAccessAccountsService.TokenResponse getToken() {
        RestClient restClient = RestClient.builder()
                .baseUrl(tokenUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("scope", "indicativefxrate:search");
        DirectAccessAccountsService.TokenResponse tokenResponse = restClient.post()
                .uri("/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(DirectAccessAccountsService.TokenResponse.class);
        return tokenResponse;
    }

    public DirectAccessAccountResponse getDirectAccessResponse(DirectAccessAccountRequest request) {
        RestClient restClient = RestClient.builder()
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
        DirectAccessAccountsService.TokenResponse response = getToken();
        System.out.println("Citizenship Verification get token Response" + response);
        restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + response.accessToken())
                .build();
        DirectAccessAccountResponse directAccessAccountResponse = createMockResponse();
        return directAccessAccountResponse;
    }

    public record DirectAccessAccountRequest(
            String family_name,
            String middle_name,
            String given_name,
            String country_of_birth,
            String place_of_birth,
            String country_of_nationality,
            String country_of_residence,
            String gbr_nino,
            String citizenship
    ) {}

    public record DirectAccessAccountResponse(
            Data data
    ) {}

    public record Data(List<Balance> balance) {}

    public record Balance(String accountId, Amount amount, String creditDebitIndicator, String type,
                          LocalDateTime dateTime, List<CreditLine> creditLine) {}

    public record Amount(String amount, String currency) {}

    public record CreditLine(boolean included, Amount amount, String type) {}
    public record TokenResponse(String accessToken,  String tokenType, Integer expiresIn) {}

    public static DirectAccessAccountsService.DirectAccessAccountResponse createMockResponse() {
        return new DirectAccessAccountsService.DirectAccessAccountResponse(
             new Data(List.of(new Balance("12345", new Amount("10000", "GBP"),
                     "Credit","ClosingAvailable", LocalDateTime.now(),
             List.of(new CreditLine(true, new Amount("10000", "GBP"), "Pre-Agreed")))))
        );
    }
}
