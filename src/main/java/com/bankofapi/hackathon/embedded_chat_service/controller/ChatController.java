package com.bankofapi.hackathon.embedded_chat_service.controller;

import com.bankofapi.hackathon.embedded_chat_service.service.ChatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/generate")
    public String generateChat(@RequestParam(value = "message") String message) {
        return chatService.getChatResponse(message);
    }
}
