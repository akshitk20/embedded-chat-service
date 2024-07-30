package com.bankofapi.hackathon.embedded_chat_service.bankofapis;

import com.bankofapi.hackathon.embedded_chat_service.bankofapis.HttpRequestFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@RestController
public class ApiController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/makeRequest")
    public String makeRequest(@RequestParam String url) {
        HttpRequestFunction<String, String> httpRequestFunction = (requestUrl) -> {
            restTemplate.setInterceptors(Collections.singletonList(new HeaderRequestInterceptor("Authorization", "ACCESS_TOKEN")));
            return restTemplate.getForObject(requestUrl, String.class);
        };

        return httpRequestFunction.apply(url);
    }
}
