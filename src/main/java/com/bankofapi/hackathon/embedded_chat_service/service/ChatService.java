package com.bankofapi.hackathon.embedded_chat_service.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatClient chatClient;

    private final WeatherService weatherService;

    public ChatService(ChatModel chatModel, WeatherService weatherService) {
        this.chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new PromptChatMemoryAdvisor(new InMemoryChatMemory()))
                .build();
        this.weatherService = weatherService;
    }

    public String getChatResponse(String message) {
        return chatClient.prompt()
                .function("WeatherService", "Get realtime weather for API", weatherService)
                .messages(new UserMessage(message))
                .call()
                .content();
    }
}
