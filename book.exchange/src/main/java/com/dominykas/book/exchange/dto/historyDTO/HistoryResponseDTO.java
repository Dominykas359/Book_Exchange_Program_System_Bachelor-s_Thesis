package com.dominykas.book.exchange.dto.historyDTO;

import com.dominykas.book.exchange.dto.noticeDTO.NoticeResponseDTO;
import com.dominykas.book.exchange.dto.publicationDTO.PublicationResponseDTO;
import com.dominykas.book.exchange.dto.userDTO.UserResponseDTO;
import com.dominykas.book.exchange.entity.enums.ExchangeRequestStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HistoryResponseDTO {
    private Long id;
    private LocalDate timeExchanged;
    private UserResponseDTO user;
    private UserResponseDTO posterUser;
    private NoticeResponseDTO notice;
    private PublicationResponseDTO givenPublication;
    private PublicationResponseDTO receivedPublication;
    private ExchangeRequestStatus status;
    private String requesterAddress;
    private String requestedFromUserAddress;
}