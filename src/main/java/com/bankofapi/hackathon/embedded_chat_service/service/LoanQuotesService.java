package com.bankofapi.hackathon.embedded_chat_service.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.function.Function;

@Service
public class LoanQuotesService implements Function<LoanQuotesService.LoanQuoteRequest, LoanQuotesService.LoanQuoteResponse> {

    @Value("${citizenshipverification.token-url}")
    private String tokenUrl;

    @Value("${citizenshipverification.api-url}")
    private String apiUrl;

    @Value("${nativeloansnwb.client-id}")
    private String clientId;

    @Value("${nativeloansnwb.client-secret}")
    private String clientSecret;

    @Override
    public LoanQuoteResponse apply(LoanQuoteRequest request) {
        return getLoanQuoteResponse(request);
    }

    public record LoanQuoteRequest(int amount, String customerId, String purposeId, int termInMonths) {}

    public record LoanQuoteResponse(String decision, PersonalLoanIllustration personalLoanIllustration, Reasons reasons) {}


    public record PersonalLoanIllustration(int apr, int interestRate, int totalRepaymentInPence, int totalInterestInPence,
                                           Repayment repayment) {}
    public record Repayment(int amountInPence, String interval) {}
    public record Reasons(String id, String code, String text) {}

    public static LoanQuotesService.LoanQuoteResponse createMockResponse() {
        return new LoanQuoteResponse(
                "ACCEPTED",
                new PersonalLoanIllustration(350, 340, 1089000, 89000
                        , new Repayment(18150, "MONTHLY"))
                ,new Reasons("ACC0010", "ACCEPT_HIGHLY_LIKELY", "Highly likely to be accepted")
        );

    }

    public static LoanQuotesService.LoanQuoteResponse createMockResponseForRejection() {
        return new LoanQuoteResponse(
                "REJECTED",
                new PersonalLoanIllustration(350, 340, 1089000000, 89000000
                        , new Repayment(181500, "MONTHLY"))
                ,new Reasons("REJ0010", "REJECT_HIGHLY_LIKELY", "Highly likely to be rejected")
        );

    }


    public record TokenResponse(@JsonProperty("access_token") String accessToken,
                                @JsonProperty("token_type") String tokenType,
                                @JsonProperty("expires_in") Integer expiresIn) {}

    public LoanQuoteResponse getLoanQuoteResponse(LoanQuoteRequest request) {
        if(request.amount < 20000) {
            return createMockResponse();
        } else {
            return createMockResponseForRejection();
        }

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
