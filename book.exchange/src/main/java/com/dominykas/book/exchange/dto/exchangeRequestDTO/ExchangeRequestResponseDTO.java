package com.dominykas.book.exchange.dto.exchangeRequestDTO;

import com.dominykas.book.exchange.dto.noticeDTO.NoticeResponseDTO;
import com.dominykas.book.exchange.dto.publicationDTO.PublicationResponseDTO;
import com.dominykas.book.exchange.dto.userDTO.UserResponseDTO;
import com.dominykas.book.exchange.entity.enums.ExchangeRequestStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExchangeRequestResponseDTO {

    private Long id;
    private LocalDate requestedTime;
    private UserResponseDTO user;
    private UserResponseDTO requestedFromUser;
    private NoticeResponseDTO notice;
    private PublicationResponseDTO givenPublication;
    private PublicationResponseDTO receivedPublication;
    private ExchangeRequestStatus status;
}