package com.dominykas.book.exchange.mapper;

import com.dominykas.book.exchange.dto.historyDTO.HistoryRequestDTO;
import com.dominykas.book.exchange.dto.historyDTO.HistoryResponseDTO;
import com.dominykas.book.exchange.entity.History;

public class HistoryMapper {

    private HistoryMapper() {}

    public static History fromDto(HistoryRequestDTO historyRequestDTO) {
        History history = new History();
        history.setId(null);
        history.setTimeExchanged(historyRequestDTO.getTimeExchanged());
        history.setStatus(historyRequestDTO.getStatus());
        return history;
    }

    public static HistoryResponseDTO toDto(History history) {
        HistoryResponseDTO historyResponseDTO = new HistoryResponseDTO();
        historyResponseDTO.setId(history.getId());
        historyResponseDTO.setTimeExchanged(history.getTimeExchanged());
        historyResponseDTO.setUser(UserMapper.toDto(history.getUser()));
        historyResponseDTO.setPosterUser(UserMapper.toDto(history.getPosterUser()));
        historyResponseDTO.setNotice(NoticeMapper.toDto(history.getNotice()));
        historyResponseDTO.setGivenPublication(PublicationMapper.toDto(history.getGivenPublication()));
        historyResponseDTO.setReceivedPublication(PublicationMapper.toDto(history.getReceivedPublication()));
        historyResponseDTO.setStatus(history.getStatus());
        return historyResponseDTO;
    }
}