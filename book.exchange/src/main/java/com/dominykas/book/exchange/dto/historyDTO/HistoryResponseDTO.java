package com.dominykas.book.exchange.dto.historyDTO;

import com.dominykas.book.exchange.dto.publicationDTO.PublicationResponseDTO;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HistoryResponseDTO {
    private Long id;
    private Long userId;
    private Long posterUserId;
    private Long noticeId;
    private LocalDate timeExchanged;
    private PublicationResponseDTO givenPublication;
    private PublicationResponseDTO receivedPublication;
}