package com.bankofapi.hackathon.embedded_chat_service.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class LoanQuotesService {

    @Value("${citizenshipverification.token-url}")
    private String tokenUrl;

    @Value("${citizenshipverification.api-url}")
    private String apiUrl;

    @Value("${nativeloansnwb.client-id}")
    private String clientId;

    @Value("${nativeloansnwb.client-secret}")
    private String clientSecret;

    public record LoanQuoteRequest(int amount, String customerId, String purposeId, int termInMonths) {}

    public record LoanQuoteResponse(String decision, PersonalLoanIllustration personalLoanIllustration, Reasons reasons) {}

    public record PersonalLoanIllustration(int apr, int interestRate, int totalRepaymentInPence, int totalInterestInPence,
                                           Repayment repayment) {}
    public record Repayment(int amountInPence, String interval) {}
    public record Reasons(String id, String code, String text) {}

    public record TokenResponse(@JsonProperty("access_token") String accessToken,
                                @JsonProperty("token_type") String tokenType,
                                @JsonProperty("expires_in") Integer expiresIn) {}

    public LoanQuoteResponse getLoanQuoteResponse(LoanQuoteRequest request) {
        RestClient restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("x-fapi-interaction-id", "98f077d4-0957-4fdf-963d-6855b2308817")
                .defaultHeader("Authorization" , "Bearer " + getToken().accessToken)
                .build();
        return restClient.post()
                .uri("/illustrations/v1/quote/personal-loan")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(LoanQuoteResponse.class);

    }
    public TokenResponse getToken() {
        RestClient restClient = RestClient.builder()
                .baseUrl(tokenUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("scope", "illustrations:create");
        TokenResponse response = restClient.post()
                .uri("/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(TokenResponse.class);
        System.out.println("Token response is " + response);
        return response;
    }
}
