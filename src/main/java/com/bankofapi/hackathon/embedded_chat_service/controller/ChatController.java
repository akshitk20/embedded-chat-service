package com.bankofapi.hackathon.embedded_chat_service.controller;

import com.bankofapi.hackathon.embedded_chat_service.service.IndicativeRatesService;
import com.bankofapi.hackathon.embedded_chat_service.service.ChatService;
import com.bankofapi.hackathon.embedded_chat_service.service.LoanQuotesService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    private final LoanQuotesService loanQuotesService;

    private final IndicativeRatesService indicativeRatesService;

    public ChatController(ChatService chatService, LoanQuotesService loanQuotesService, IndicativeRatesService indicativeRatesService) {
        this.chatService = chatService;
        this.loanQuotesService = loanQuotesService;
        this.indicativeRatesService = indicativeRatesService;
    }

    @GetMapping("/generate")
    public String generateChat(@RequestParam(value = "message") String message) {
        return chatService.getChatResponse(message);
    }

    @PostMapping("/loan")
    public LoanQuotesService.LoanQuoteResponse getTokenResponse(@RequestBody LoanQuotesService.LoanQuoteRequest request) {
        return loanQuotesService.getLoanQuoteResponse(request);
    }

    @GetMapping("/fx-indicative")
    public IndicativeRatesService.FxIndicativeRatesResponse getToken(@RequestParam(value = "fromCurrency") String fromCurrency,
                                                                     @RequestParam(value = "toCurrency") String toCurrency) {
        return indicativeRatesService.getFxIndicativeRateResponse(fromCurrency, toCurrency);
    }
}
