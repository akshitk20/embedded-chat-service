package com.bankofapi.hackathon.embedded_chat_service.bankofapis;

@FunctionalInterface
public interface HttpRequestFunction<T, R> {
    R apply(T request);
}
