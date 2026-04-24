package com.dominykas.book.exchange.controller;

import com.dominykas.book.exchange.dto.noticeDTO.NoticeFilterDTO;
import com.dominykas.book.exchange.dto.noticeDTO.NoticeRequestDTO;
import com.dominykas.book.exchange.dto.noticeDTO.NoticeResponseDTO;
import com.dominykas.book.exchange.dto.noticeDTO.PageResponseDTO;
import com.dominykas.book.exchange.dto.publicationDTO.PublicationRequestDTO;
import com.dominykas.book.exchange.service.NoticeService;
import com.dominykas.book.exchange.service.PublicationService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void getNotices_WithNullFilters_ShouldWork() {
        @SuppressWarnings("unchecked")
        PageResponseDTO<NoticeResponseDTO> response = mock(PageResponseDTO.class);

        when(noticeService.getNotices(anyInt(), anyInt(), anyString(), anyString(), any()))
                .thenReturn(response);

        PageResponseDTO<NoticeResponseDTO> result = controller.getNotices(
                0, 15, "timePosted", "desc",
                null, null, null, null,
                null, null, null, null,
                null, null, null, null
        );

        assertThat(result).isSameAs(response);
        verify(noticeService).getNotices(eq(0), eq(15), eq("timePosted"), eq("desc"), any());
    }

    @Test
    void getNotices_ShouldMapFilterCorrectly() {
        @SuppressWarnings("unchecked")
        PageResponseDTO<NoticeResponseDTO> response = mock(PageResponseDTO.class);

        when(noticeService.getNotices(anyInt(), anyInt(), anyString(), anyString(), any()))
                .thenReturn(response);

        controller.getNotices(
                1, 10, "title", "asc",
                5L, "test", "author", "EN",
                LocalDate.of(2000, 1, 1),
                LocalDate.of(2020, 1, 1),
                100, 300, "hard", true,
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31)
        );

        ArgumentCaptor<NoticeFilterDTO> captor = ArgumentCaptor.forClass(NoticeFilterDTO.class);

        verify(noticeService).getNotices(eq(1), eq(10), eq("title"), eq("asc"), captor.capture());

        NoticeFilterDTO filter = captor.getValue();

        assertThat(filter.getPosterId()).isEqualTo(5L);
        assertThat(filter.getTitle()).isEqualTo("test");
        assertThat(filter.getAuthor()).isEqualTo("author");
        assertThat(filter.getLanguage()).isEqualTo("EN");
        assertThat(filter.getMinPageCount()).isEqualTo(100);
        assertThat(filter.getMaxPageCount()).isEqualTo(300);
        assertThat(filter.getColored()).isTrue();
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
    void getNoticeById_WhenServiceThrows_ShouldPropagate() {
        when(noticeService.getNoticeById(1L))
                .thenThrow(new RuntimeException("Not found"));

        assertThrows(RuntimeException.class, () -> controller.getNoticeById(1L));
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