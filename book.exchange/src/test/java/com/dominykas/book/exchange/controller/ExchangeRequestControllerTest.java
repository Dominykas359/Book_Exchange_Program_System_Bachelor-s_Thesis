package com.dominykas.book.exchange.controller;

import com.dominykas.book.exchange.dto.exchangeRequestDTO.AcceptExchangeRequestDTO;
import com.dominykas.book.exchange.dto.exchangeRequestDTO.ExchangeRequestRequestDTO;
import com.dominykas.book.exchange.dto.exchangeRequestDTO.ExchangeRequestResponseDTO;
import com.dominykas.book.exchange.entity.enums.ExchangeRequestStatus;
import com.dominykas.book.exchange.service.ExchangeRequestService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRequestControllerTest {

    @Mock
    private ExchangeRequestService exchangeRequestService;

    @InjectMocks
    private ExchangeRequestController controller;

    @Test
    void createExchangeRequest_ShouldReturnResponse() {
        ExchangeRequestRequestDTO request = new ExchangeRequestRequestDTO();
        ExchangeRequestResponseDTO response = new ExchangeRequestResponseDTO();

        when(exchangeRequestService.createExchangeRequest(request)).thenReturn(response);

        ExchangeRequestResponseDTO result = controller.createExchangeRequest(request);

        assertThat(result).isSameAs(response);
        verify(exchangeRequestService).createExchangeRequest(request);
    }

    @Test
    void getExchangeRequestById_ShouldReturnResponse() {
        ExchangeRequestResponseDTO response = new ExchangeRequestResponseDTO();

        when(exchangeRequestService.getExchangeRequestById(1L)).thenReturn(response);

        ExchangeRequestResponseDTO result = controller.getExchangeRequestById(1L);

        assertThat(result).isSameAs(response);
        verify(exchangeRequestService).getExchangeRequestById(1L);
    }

    @Test
    void getExchangeRequestsByUserId_ShouldReturnList() {
        List<ExchangeRequestResponseDTO> response = List.of(new ExchangeRequestResponseDTO());

        when(exchangeRequestService.getExchangeRequestsByUserId(1L)).thenReturn(response);

        List<ExchangeRequestResponseDTO> result = controller.getExchangeRequestsByUserId(1L);

        assertThat(result).isSameAs(response);
        verify(exchangeRequestService).getExchangeRequestsByUserId(1L);
    }

    @Test
    void getExchangeRequestsByNoticeId_ShouldReturnList() {
        List<ExchangeRequestResponseDTO> response = List.of(new ExchangeRequestResponseDTO());

        when(exchangeRequestService.getExchangeRequestsByNoticeId(1L)).thenReturn(response);

        List<ExchangeRequestResponseDTO> result = controller.getExchangeRequestsByNoticeId(1L);

        assertThat(result).isSameAs(response);
        verify(exchangeRequestService).getExchangeRequestsByNoticeId(1L);
    }

    @Test
    void getExchangeRequestsByStatus_ShouldReturnList() {
        List<ExchangeRequestResponseDTO> response = List.of(new ExchangeRequestResponseDTO());

        when(exchangeRequestService.getExchangeRequestsByStatus(ExchangeRequestStatus.PENDING))
                .thenReturn(response);

        List<ExchangeRequestResponseDTO> result =
                controller.getExchangeRequestsByStatus(ExchangeRequestStatus.PENDING);

        assertThat(result).isSameAs(response);
        verify(exchangeRequestService).getExchangeRequestsByStatus(ExchangeRequestStatus.PENDING);
    }

    @Test
    void acceptExchangeRequest_ShouldReturnResponse() {
        AcceptExchangeRequestDTO request = new AcceptExchangeRequestDTO();
        ExchangeRequestResponseDTO response = new ExchangeRequestResponseDTO();

        when(exchangeRequestService.acceptExchangeRequest(1L, request)).thenReturn(response);

        ExchangeRequestResponseDTO result = controller.acceptExchangeRequest(1L, request);

        assertThat(result).isSameAs(response);
        verify(exchangeRequestService).acceptExchangeRequest(1L, request);
    }

    @Test
    void declineExchangeRequest_ShouldReturnResponse() {
        ExchangeRequestResponseDTO response = new ExchangeRequestResponseDTO();

        when(exchangeRequestService.declineExchangeRequest(1L)).thenReturn(response);

        ExchangeRequestResponseDTO result = controller.declineExchangeRequest(1L);

        assertThat(result).isSameAs(response);
        verify(exchangeRequestService).declineExchangeRequest(1L);
    }

    @Test
    void deleteExchangeRequest_ShouldCallService() {
        controller.deleteExchangeRequest(1L);

        verify(exchangeRequestService).deleteExchangeRequest(1L);
    }
}