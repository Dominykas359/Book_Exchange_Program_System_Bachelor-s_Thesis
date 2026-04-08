package com.dominykas.book.exchange.controller;

import com.dominykas.book.exchange.dto.aiChatDTO.AiChatMessageRequestDTO;
import com.dominykas.book.exchange.dto.aiChatDTO.AiChatMessageResponseDTO;
import com.dominykas.book.exchange.service.AiChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ai-chat")
@RequiredArgsConstructor
public class AiChatMessageController {

    private final AiChatMessageService aiChatMessageService;

    @PostMapping("/messages")
    public AiChatMessageResponseDTO createMessage(@RequestBody AiChatMessageRequestDTO aiChatMessageRequestDTO) {
        return aiChatMessageService.createMessage(aiChatMessageRequestDTO);
    }

    @GetMapping("/history/{userId}")
    public List<AiChatMessageResponseDTO> getHistory(@PathVariable Long userId) {
        return aiChatMessageService.getMessagesByUserId(userId);
    }

    @DeleteMapping("/history/{userId}")
    public void clearHistory(@PathVariable Long userId) {
        aiChatMessageService.clearHistory(userId);
    }
}