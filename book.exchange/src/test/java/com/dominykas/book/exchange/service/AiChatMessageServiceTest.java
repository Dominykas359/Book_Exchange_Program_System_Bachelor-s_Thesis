package com.dominykas.book.exchange.service;

import com.dominykas.book.exchange.dto.aiChatDTO.AiChatMessageRequestDTO;
import com.dominykas.book.exchange.dto.aiChatDTO.AiChatMessageResponseDTO;
import com.dominykas.book.exchange.entity.AiChatMessage;
import com.dominykas.book.exchange.entity.Notice;
import com.dominykas.book.exchange.entity.Publication;
import com.dominykas.book.exchange.entity.User;
import com.dominykas.book.exchange.repository.AiChatMessageRepository;
import com.dominykas.book.exchange.repository.NoticeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiChatMessageServiceTest {

    @Mock
    private AiChatMessageRepository aiChatMessageRepository;

    @Mock
    private NoticeRepository noticeRepository;

    @InjectMocks
    private AiChatMessageService service;

    @Test
    void createMessage_ShouldSaveMessage() {
        AiChatMessageRequestDTO request = new AiChatMessageRequestDTO();
        request.setUserId(1L);
        request.setRole(" user ");
        request.setText(" hello ");
        request.setNoticeIds(List.of());

        AiChatMessage saved = new AiChatMessage();
        saved.setId(1L);
        saved.setUserId(1L);
        saved.setRole("user");
        saved.setText("hello");

        when(aiChatMessageRepository.save(any(AiChatMessage.class))).thenReturn(saved);

        AiChatMessageResponseDTO result = service.createMessage(request);

        assertThat(result).isNotNull();

        verify(aiChatMessageRepository).save(any(AiChatMessage.class));
        verifyNoInteractions(noticeRepository);
    }

    @Test
    void createMessage_WithNoticeIds_ShouldFetchAndAttachNotices() {
        AiChatMessageRequestDTO request = new AiChatMessageRequestDTO();
        request.setUserId(1L);
        request.setRole("assistant");
        request.setText("text");
        request.setNoticeIds(List.of(2L, 1L));

        User poster = User.builder()
                .id(10L)
                .email("poster@gmail.com")
                .firstName("Poster")
                .lastName("User")
                .build();

        Publication publication1 = new Publication();
        publication1.setId(101L);
        publication1.setTitle("Book 1");

        Publication publication2 = new Publication();
        publication2.setId(102L);
        publication2.setTitle("Book 2");

        Notice notice1 = new Notice();
        notice1.setId(1L);
        notice1.setPoster(poster);
        notice1.setPublication(publication1);

        Notice notice2 = new Notice();
        notice2.setId(2L);
        notice2.setPoster(poster);
        notice2.setPublication(publication2);

        AiChatMessage saved = new AiChatMessage();
        saved.setId(1L);
        saved.setUserId(1L);
        saved.setRole("assistant");
        saved.setText("text");
        saved.setNotices(List.of(notice2, notice1));

        when(noticeRepository.findAllById(List.of(2L, 1L))).thenReturn(List.of(notice1, notice2));
        when(aiChatMessageRepository.save(any(AiChatMessage.class))).thenReturn(saved);

        AiChatMessageResponseDTO result = service.createMessage(request);

        assertThat(result).isNotNull();

        verify(noticeRepository).findAllById(List.of(2L, 1L));
        verify(aiChatMessageRepository).save(any(AiChatMessage.class));
    }

    @Test
    void getMessagesByUserId_ShouldReturnMessages() {
        AiChatMessage message = new AiChatMessage();
        message.setId(1L);
        message.setUserId(1L);
        message.setRole("user");
        message.setText("hello");

        when(aiChatMessageRepository.findAllByUserIdOrderByCreatedAtAscIdAsc(1L))
                .thenReturn(List.of(message));

        List<AiChatMessageResponseDTO> result = service.getMessagesByUserId(1L);

        assertThat(result).hasSize(1);

        verify(aiChatMessageRepository).findAllByUserIdOrderByCreatedAtAscIdAsc(1L);
    }

    @Test
    void clearHistory_ShouldDeleteByUserId() {
        service.clearHistory(1L);

        verify(aiChatMessageRepository).deleteAllByUserId(1L);
    }
}