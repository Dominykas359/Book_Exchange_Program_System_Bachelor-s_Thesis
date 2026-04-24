package com.dominykas.book.exchange.controller;

import com.dominykas.book.exchange.dto.noticeDTO.NoticeRequestDTO;
import com.dominykas.book.exchange.dto.noticeDTO.NoticeResponseDTO;
import com.dominykas.book.exchange.dto.noticeDTO.PageResponseDTO;
import com.dominykas.book.exchange.service.NoticeService;
import com.dominykas.book.exchange.service.PublicationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoticeControllerTest {

    @Mock
    private NoticeService noticeService;

    @Mock
    private PublicationService publicationService;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private NoticeController controller;

    @Test
    void createNotice_ShouldReturnResponse() {
        NoticeRequestDTO request = new NoticeRequestDTO();
        NoticeResponseDTO response = new NoticeResponseDTO();

        when(noticeService.createNotice(request)).thenReturn(response);

        NoticeResponseDTO result = controller.createNotice(request);

        assertThat(result).isSameAs(response);
        verify(noticeService).createNotice(request);
    }

    @Test
    void getNotices_ShouldReturnPageResponse() {
        @SuppressWarnings("unchecked")
        PageResponseDTO<NoticeResponseDTO> response = mock(PageResponseDTO.class);

        when(noticeService.getNotices(anyInt(), anyInt(), anyString(), anyString(), any()))
                .thenReturn(response);

        PageResponseDTO<NoticeResponseDTO> result = controller.getNotices(
                0,
                15,
                "timePosted",
                "desc",
                1L,
                "title",
                "author",
                "English",
                LocalDate.of(2000, 1, 1),
                LocalDate.of(2024, 1, 1),
                100,
                500,
                "cover",
                true,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31)
        );

        assertThat(result).isSameAs(response);
        verify(noticeService).getNotices(eq(0), eq(15), eq("timePosted"), eq("desc"), any());
    }

    @Test
    void getNoticeById_ShouldReturnResponse() {
        NoticeResponseDTO response = new NoticeResponseDTO();

        when(noticeService.getNoticeById(1L)).thenReturn(response);

        NoticeResponseDTO result = controller.getNoticeById(1L);

        assertThat(result).isSameAs(response);
        verify(noticeService).getNoticeById(1L);
    }

    @Test
    void getNoticeByPublicationId_ShouldReturnResponse() {
        NoticeResponseDTO response = new NoticeResponseDTO();

        when(noticeService.getNoticeByPublicationId(1L)).thenReturn(response);

        NoticeResponseDTO result = controller.getNoticeByPublicationId(1L);

        assertThat(result).isSameAs(response);
        verify(noticeService).getNoticeByPublicationId(1L);
    }

    @Test
    void getNoticesByPosterId_ShouldReturnList() {
        List<NoticeResponseDTO> response = List.of(new NoticeResponseDTO());

        when(noticeService.getNoticesByPosterId(1L)).thenReturn(response);

        List<NoticeResponseDTO> result = controller.getNoticesByPosterId(1L);

        assertThat(result).isSameAs(response);
        verify(noticeService).getNoticesByPosterId(1L);
    }
}