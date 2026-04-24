package com.dominykas.book.exchange.controller;

import com.dominykas.book.exchange.dto.aiChatDTO.AiChatMessageRequestDTO;
import com.dominykas.book.exchange.dto.aiChatDTO.AiChatMessageResponseDTO;
import com.dominykas.book.exchange.service.AiChatMessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiChatMessageControllerTest {

    @Mock
    private AiChatMessageService aiChatMessageService;

    @InjectMocks
    private AiChatMessageController controller;

    @Test
    void createMessage_ShouldReturnResponse() {
        AiChatMessageRequestDTO request = new AiChatMessageRequestDTO();
        AiChatMessageResponseDTO response = new AiChatMessageResponseDTO();

        when(aiChatMessageService.createMessage(request)).thenReturn(response);

        AiChatMessageResponseDTO result = controller.createMessage(request);

        assertThat(result).isSameAs(response);
        verify(aiChatMessageService).createMessage(request);
    }

    @Test
    void getHistory_ShouldReturnMessages() {
        List<AiChatMessageResponseDTO> response = List.of(new AiChatMessageResponseDTO());

        when(aiChatMessageService.getMessagesByUserId(1L)).thenReturn(response);

        List<AiChatMessageResponseDTO> result = controller.getHistory(1L);

        assertThat(result).isSameAs(response);
        verify(aiChatMessageService).getMessagesByUserId(1L);
    }

    @Test
    void clearHistory_ShouldCallService() {
        controller.clearHistory(1L);

        verify(aiChatMessageService).clearHistory(1L);
    }
}