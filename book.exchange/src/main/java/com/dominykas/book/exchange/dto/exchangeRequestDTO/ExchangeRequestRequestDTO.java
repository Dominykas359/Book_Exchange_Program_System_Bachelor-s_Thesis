package com.dominykas.book.exchange.dto.exchangeRequestDTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ExchangeRequestRequestDTO {

    private LocalDate requestedTime;
    private Long userId;
    private Long requestedFromUserId;
    private Long noticeId;
    private Long givenPublicationId;
    private Long receivedPublicationId;
    private String requesterAddress;
    private String requestedFromUserAddress;
}