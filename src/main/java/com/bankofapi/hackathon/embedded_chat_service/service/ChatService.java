package com.bankofapi.hackathon.embedded_chat_service.service;

import com.bankofapi.hackathon.embedded_chat_service.service.bankofapi.CitizenshipVerification;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatClient chatClient;

    private final CitizenshipVerification citizenshipVerificationService;

    public ChatService(ChatModel chatModel, CitizenshipVerification citizenshipVerificationService) {
        this.chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new PromptChatMemoryAdvisor(new InMemoryChatMemory()))
                .build();
        this.citizenshipVerificationService = citizenshipVerificationService;
    }

    public String getChatResponse(String message) {
        return chatClient.prompt()
                .function("CitizenshipVerificationService", "Get Details for the User", citizenshipVerificationService)
                .messages(new UserMessage(message))
                .call()
                .content();
    }
}
