package com.dominykas.book.exchange.controller;

import com.dominykas.book.exchange.dto.historyDTO.HistoryRequestDTO;
import com.dominykas.book.exchange.dto.historyDTO.HistoryResponseDTO;
import com.dominykas.book.exchange.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @PostMapping
    public HistoryResponseDTO createHistory(@RequestBody HistoryRequestDTO historyRequestDTO) {
        return historyService.createHistory(historyRequestDTO);
    }

    @GetMapping("/{id}")
    public HistoryResponseDTO getHistoryById(@PathVariable Long id) {
        return historyService.getHistoryById(id);
    }

    @GetMapping("/user/{userId}")
    public List<HistoryResponseDTO> getHistoryByUserId(@PathVariable Long userId) {
        return historyService.getHistoryByUserId(userId);
    }

    @GetMapping("/me")
    public List<HistoryResponseDTO> getMyHistory(Principal principal) {
        return historyService.getMyHistory(principal);
    }
}