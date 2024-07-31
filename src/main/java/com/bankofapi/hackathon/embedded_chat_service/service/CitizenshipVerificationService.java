package com.bankofapi.hackathon.embedded_chat_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class CitizenshipVerificationService implements Function<CitizenshipVerificationService.CitizenshipVerificationRequest, CitizenshipVerificationService.CitizenshipVerificationResponse> {


    @Value("${citizenshipverification.api-url}")
    private String apiUrl;


    @Value("${citizenshipverification.token-url}")
    private String tokenUrl;

    @Value("${citizenshipverification.client-id}")
    private String clientId;

    @Value("${citizenshipverification.client-secret}")
    private String clientSecret;

    @Override
    public CitizenshipVerificationResponse apply(CitizenshipVerificationRequest request) {
        return getCitizenShipVerificationResponse(request);
    }

    public CitizenshipVerificationService.TokenResponse getToken() {
        RestClient restClient = RestClient.builder()
                .baseUrl(tokenUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("scope", "indicativefxrate:search");
        CitizenshipVerificationService.TokenResponse tokenResponse = restClient.post()
                .uri("/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(CitizenshipVerificationService.TokenResponse.class);
        return tokenResponse;
    }

    public CitizenshipVerificationResponse getCitizenShipVerificationResponse(CitizenshipVerificationRequest request) {
        RestClient restClient = RestClient.builder()
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
        TokenResponse response = getToken();
        System.out.println("Citizenship Verification get token Response" + response);
        restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + response.accessToken())
                .build();
        CitizenshipVerificationResponse citizenshipVerificationResponse = createMockResponse();
        return citizenshipVerificationResponse;
    }

    public record CitizenshipVerificationRequest(
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

    public record CitizenshipVerificationResponse(
            boolean given_name,
            boolean middle_name,
            boolean family_name,
            boolean country_of_birth,
            boolean place_of_birth,
            boolean country_of_nationality,
            boolean country_of_residence,
            boolean gbr_nino,
            boolean citizenship
    ) {}
    public record TokenResponse(String accessToken,  String tokenType, Integer expiresIn) {}

    public static CitizenshipVerificationService.CitizenshipVerificationResponse createMockResponse() {
        return new CitizenshipVerificationService.CitizenshipVerificationResponse(
                true,  // given_name
                true,  // middle_name
                true,  // family_name
                false, // country_of_birth
                false, // place_of_birth
                true,  // country_of_nationality
                false, // country_of_residence
                true,  // gbr_nino
                true   // citizenship
        );
    }
}
