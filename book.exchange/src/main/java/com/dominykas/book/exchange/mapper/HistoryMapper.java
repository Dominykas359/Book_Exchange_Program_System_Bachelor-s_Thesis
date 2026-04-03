package com.dominykas.book.exchange.mapper;

import com.dominykas.book.exchange.dto.historyDTO.HistoryRequestDTO;
import com.dominykas.book.exchange.dto.historyDTO.HistoryResponseDTO;
import com.dominykas.book.exchange.entity.History;

public class HistoryMapper {

    private HistoryMapper() {}

    public static History fromDto(HistoryRequestDTO historyRequestDTO) {
        History history = new History();
        history.setId(null);
        history.setUserId(historyRequestDTO.getUserId());
        history.setPosterUserId(historyRequestDTO.getPosterUserId());
        history.setNoticeId(historyRequestDTO.getNoticeId());
        history.setTimeExchanged(historyRequestDTO.getTimeExchanged());
        return history;
    }

    public static HistoryResponseDTO toDto(History history) {
        HistoryResponseDTO historyResponseDTO = new HistoryResponseDTO();
        historyResponseDTO.setId(history.getId());
        historyResponseDTO.setUserId(history.getUserId());
        historyResponseDTO.setPosterUserId(history.getPosterUserId());
        historyResponseDTO.setNoticeId(history.getNoticeId());
        historyResponseDTO.setTimeExchanged(history.getTimeExchanged());
        historyResponseDTO.setGivenPublication(PublicationMapper.toDto(history.getGivenPublication()));
        historyResponseDTO.setReceivedPublication(PublicationMapper.toDto(history.getReceivedPublication()));
        return historyResponseDTO;
    }
}