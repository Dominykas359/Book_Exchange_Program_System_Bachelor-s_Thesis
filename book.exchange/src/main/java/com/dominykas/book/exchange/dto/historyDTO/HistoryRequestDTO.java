package com.dominykas.book.exchange.dto.historyDTO;

import com.dominykas.book.exchange.entity.enums.ExchangeRequestStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HistoryRequestDTO {
    private Long userId;
    private Long posterUserId;
    private Long noticeId;
    private LocalDate timeExchanged;
    private Long givenPublicationId;
    private Long receivedPublicationId;
    private ExchangeRequestStatus status;
}