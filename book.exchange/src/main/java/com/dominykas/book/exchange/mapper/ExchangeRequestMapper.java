package com.dominykas.book.exchange.mapper;

import com.dominykas.book.exchange.dto.exchangeRequestDTO.ExchangeRequestRequestDTO;
import com.dominykas.book.exchange.dto.exchangeRequestDTO.ExchangeRequestResponseDTO;
import com.dominykas.book.exchange.entity.ExchangeRequest;

public class ExchangeRequestMapper {

    private ExchangeRequestMapper() {}

    public static ExchangeRequest fromDto(ExchangeRequestRequestDTO dto) {
        ExchangeRequest exchangeRequest = new ExchangeRequest();
        exchangeRequest.setRequestedTime(dto.getRequestedTime());
        return exchangeRequest;
    }

    public static ExchangeRequestResponseDTO toDto(ExchangeRequest exchangeRequest) {
        ExchangeRequestResponseDTO dto = new ExchangeRequestResponseDTO();
        dto.setId(exchangeRequest.getId());
        dto.setRequestedTime(exchangeRequest.getRequestedTime());
        dto.setUser(UserMapper.toDto(exchangeRequest.getUser()));
        dto.setRequestedFromUser(UserMapper.toDto(exchangeRequest.getRequestedFromUser()));
        dto.setNotice(NoticeMapper.toDto(exchangeRequest.getNotice()));
        dto.setGivenPublication(PublicationMapper.toDto(exchangeRequest.getGivenPublication()));
        dto.setReceivedPublication(PublicationMapper.toDto(exchangeRequest.getReceivedPublication()));
        dto.setStatus(exchangeRequest.getStatus());
        return dto;
    }
}