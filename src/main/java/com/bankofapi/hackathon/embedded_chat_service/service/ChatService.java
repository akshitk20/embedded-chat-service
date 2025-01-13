package com.bankofapi.hackathon.embedded_chat_service.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    private final ChatClient chatClient;

    private final CitizenshipVerificationService citizenshipVerificationService;

    private final LoanQuotesService loanQuotesService;

    private final DirectAccessAccountsService directAccessAccountsService;

    private final VectorStore vectorStore;

    @Value("classpath:/prompts/natwest-reference.st")
    private Resource resource;

    public ChatService(ChatModel chatModel, CitizenshipVerificationService citizenshipVerificationService, LoanQuotesService loanQuotesService, DirectAccessAccountsService directAccessAccountsService, VectorStore vectorStore) {
        this.chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new PromptChatMemoryAdvisor(new InMemoryChatMemory()))
                .build();
        this.citizenshipVerificationService = citizenshipVerificationService;
        this.loanQuotesService = loanQuotesService;
        this.directAccessAccountsService = directAccessAccountsService;
        this.vectorStore = vectorStore;
    }

    public String getChatResponse(String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("input", message);
        map.put("documents", String.join("/n", findSimilarDocuments(message)));
        PromptTemplate promptTemplate = new PromptTemplate(resource, map);
        Prompt prompt = promptTemplate.create();
        return chatClient.prompt(prompt)
                .function("CitizenshipVerificationService", "Check citizenship details for the user", citizenshipVerificationService)
                .function("DirectAccessAccountsService", "Accounts access", directAccessAccountsService)
                .function("LoanQuoteService", "Check Loan", loanQuotesService)
                .messages(new UserMessage(message))
                .call()
                .content();
    }

    private List<String> findSimilarDocuments(String message) {
        List<Document> documents = vectorStore.similaritySearch(SearchRequest.query(message).withTopK(2));
        return documents.stream()
                .map(Document::getContent)
                .toList();
    }


}
