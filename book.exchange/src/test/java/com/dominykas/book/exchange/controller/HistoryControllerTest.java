package com.dominykas.book.exchange.controller;

import com.dominykas.book.exchange.dto.historyDTO.HistoryRequestDTO;
import com.dominykas.book.exchange.dto.historyDTO.HistoryResponseDTO;
import com.dominykas.book.exchange.service.HistoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoryControllerTest {

    @Mock
    private HistoryService historyService;

    @InjectMocks
    private HistoryController controller;

    @Test
    void createHistory_ShouldReturnResponse() {
        HistoryRequestDTO request = new HistoryRequestDTO();
        HistoryResponseDTO response = new HistoryResponseDTO();

        when(historyService.createHistory(request)).thenReturn(response);

        HistoryResponseDTO result = controller.createHistory(request);

        assertThat(result).isSameAs(response);
        verify(historyService).createHistory(request);
    }

    @Test
    void getHistoryById_ShouldReturnResponse() {
        HistoryResponseDTO response = new HistoryResponseDTO();

        when(historyService.getHistoryById(1L)).thenReturn(response);

        HistoryResponseDTO result = controller.getHistoryById(1L);

        assertThat(result).isSameAs(response);
        verify(historyService).getHistoryById(1L);
    }

    @Test
    void getHistoryByUserId_ShouldReturnList() {
        List<HistoryResponseDTO> response = List.of(new HistoryResponseDTO());

        when(historyService.getHistoryByUserId(1L)).thenReturn(response);

        List<HistoryResponseDTO> result = controller.getHistoryByUserId(1L);

        assertThat(result).isSameAs(response);
        verify(historyService).getHistoryByUserId(1L);
    }
}