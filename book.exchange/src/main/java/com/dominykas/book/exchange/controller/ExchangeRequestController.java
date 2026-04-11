package com.dominykas.book.exchange.controller;

import com.dominykas.book.exchange.dto.exchangeRequestDTO.ExchangeRequestRequestDTO;
import com.dominykas.book.exchange.dto.exchangeRequestDTO.ExchangeRequestResponseDTO;
import com.dominykas.book.exchange.entity.enums.ExchangeRequestStatus;
import com.dominykas.book.exchange.service.ExchangeRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exchange-requests")
@RequiredArgsConstructor
public class ExchangeRequestController {

    private final ExchangeRequestService exchangeRequestService;

    @PostMapping
    public ExchangeRequestResponseDTO createExchangeRequest(@RequestBody ExchangeRequestRequestDTO dto) {
        return exchangeRequestService.createExchangeRequest(dto);
    }

    @GetMapping("/{id}")
    public ExchangeRequestResponseDTO getExchangeRequestById(@PathVariable Long id) {
        return exchangeRequestService.getExchangeRequestById(id);
    }

    @GetMapping("/user/{userId}")
    public List<ExchangeRequestResponseDTO> getExchangeRequestsByUserId(@PathVariable Long userId) {
        return exchangeRequestService.getExchangeRequestsByUserId(userId);
    }

    @GetMapping("/notice/{noticeId}")
    public List<ExchangeRequestResponseDTO> getExchangeRequestsByNoticeId(@PathVariable Long noticeId) {
        return exchangeRequestService.getExchangeRequestsByNoticeId(noticeId);
    }

    @GetMapping("/status/{status}")
    public List<ExchangeRequestResponseDTO> getExchangeRequestsByStatus(@PathVariable ExchangeRequestStatus status) {
        return exchangeRequestService.getExchangeRequestsByStatus(status);
    }

    @PatchMapping("/{id}/accept")
    public ExchangeRequestResponseDTO acceptExchangeRequest(@PathVariable Long id) {
        return exchangeRequestService.acceptExchangeRequest(id);
    }

    @PatchMapping("/{id}/decline")
    public ExchangeRequestResponseDTO declineExchangeRequest(@PathVariable Long id) {
        return exchangeRequestService.declineExchangeRequest(id);
    }

    @DeleteMapping("/{id}")
    public void deleteExchangeRequest(@PathVariable Long id) {
        exchangeRequestService.deleteExchangeRequest(id);
    }
}