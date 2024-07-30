package com.bankofapi.hackathon.embedded_chat_service.bankofapis;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Component
public class WireMockInitializer {

    @Autowired
    private WireMockServer wireMockServer;

    @PostConstruct
    public void setupMockResponses() {
        wireMockServer.stubFor(get(urlEqualTo("/posts/1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":1,\"title\":\"mock title\"}")));
    }
}