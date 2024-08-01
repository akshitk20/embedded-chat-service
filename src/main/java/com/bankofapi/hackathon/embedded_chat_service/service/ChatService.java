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

    private final CitizenshipVerificationService citizenshipVerificationService;

    private final LoanQuotesService loanQuotesService;

    private final DirectAccessAccountsService directAccessAccountsService;

    public ChatService(ChatModel chatModel, CitizenshipVerificationService citizenshipVerificationService, LoanQuotesService loanQuotesService, DirectAccessAccountsService directAccessAccountsService) {
        this.chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new PromptChatMemoryAdvisor(new InMemoryChatMemory()))
                .build();
        this.citizenshipVerificationService = citizenshipVerificationService;
        this.loanQuotesService = loanQuotesService;
        this.directAccessAccountsService = directAccessAccountsService;
    }

    public String getChatResponse(String message) {
        return chatClient.prompt()
                .function("CitizenshipVerificationService", "Check citizenship details for the user", citizenshipVerificationService)
                .function("DirectAccessAccountsService", "Accounts access", directAccessAccountsService)
                .function("LoanQuoteService", "Check Loan", loanQuotesService)
                .messages(new UserMessage(message))
                .call()
                .content();
    }
}
