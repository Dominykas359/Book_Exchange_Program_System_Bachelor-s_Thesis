package com.dominykas.book.exchange.service;

import com.dominykas.book.exchange.dto.noticeDTO.NoticeFilterDTO;
import com.dominykas.book.exchange.dto.noticeDTO.NoticeRequestDTO;
import com.dominykas.book.exchange.dto.noticeDTO.NoticeResponseDTO;
import com.dominykas.book.exchange.dto.noticeDTO.PageResponseDTO;
import com.dominykas.book.exchange.entity.Notice;
import com.dominykas.book.exchange.entity.Publication;
import com.dominykas.book.exchange.entity.User;
import com.dominykas.book.exchange.entity.enums.NoticeStatus;
import com.dominykas.book.exchange.repository.NoticeRepository;
import com.dominykas.book.exchange.repository.PublicationRepository;
import com.dominykas.book.exchange.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @Mock private NoticeRepository noticeRepository;
    @Mock private UserRepository userRepository;
    @Mock private PublicationRepository publicationRepository;

    @InjectMocks
    private NoticeService noticeService;

    @Test
    void createNotice_ShouldCreateNotice() {
        NoticeRequestDTO request = new NoticeRequestDTO();
        request.setPosterId(1L);
        request.setPublicationId(2L);

        User poster = User.builder().id(1L).email("poster@gmail.com").firstName("A").lastName("B").build();

        Publication publication = new Publication();
        publication.setId(2L);
        publication.setTitle("Book");

        Notice saved = new Notice();
        saved.setId(10L);
        saved.setPoster(poster);
        saved.setPublication(publication);
        saved.setStatus(NoticeStatus.OPEN);
        saved.setTimePosted(LocalDate.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(poster));
        when(publicationRepository.findById(2L)).thenReturn(Optional.of(publication));
        when(noticeRepository.save(any(Notice.class))).thenReturn(saved);

        NoticeResponseDTO result = noticeService.createNotice(request);

        assertThat(result).isNotNull();
        verify(noticeRepository).save(any(Notice.class));
    }

    @Test
    void createNotice_WhenUserMissing_ShouldThrowException() {
        NoticeRequestDTO request = new NoticeRequestDTO();
        request.setPosterId(1L);
        request.setPublicationId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> noticeService.createNotice(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void getNotices_ShouldReturnPageResponse() {
        NoticeFilterDTO filter = new NoticeFilterDTO();

        when(noticeRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        PageResponseDTO<NoticeResponseDTO> result =
                noticeService.getNotices(0, 15, "timePosted", "desc", filter);

        assertThat(result).isNotNull();
        verify(noticeRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getNoticeById_ShouldReturnNotice() {
        User poster = User.builder().id(1L).email("poster@gmail.com").firstName("A").lastName("B").build();

        Publication publication = new Publication();
        publication.setId(2L);

        Notice notice = new Notice();
        notice.setId(10L);
        notice.setPoster(poster);
        notice.setPublication(publication);

        when(noticeRepository.findById(10L)).thenReturn(Optional.of(notice));

        NoticeResponseDTO result = noticeService.getNoticeById(10L);

        assertThat(result).isNotNull();
        verify(noticeRepository).findById(10L);
    }

    @Test
    void getNoticeByPublicationId_ShouldReturnNotice() {
        User poster = User.builder().id(1L).email("poster@gmail.com").firstName("A").lastName("B").build();

        Publication publication = new Publication();
        publication.setId(2L);

        Notice notice = new Notice();
        notice.setId(10L);
        notice.setPoster(poster);
        notice.setPublication(publication);

        when(noticeRepository.findByPublicationId(2L)).thenReturn(Optional.of(notice));

        NoticeResponseDTO result = noticeService.getNoticeByPublicationId(2L);

        assertThat(result).isNotNull();
        verify(noticeRepository).findByPublicationId(2L);
    }

    @Test
    void getNoticesByPosterId_ShouldReturnList() {
        when(noticeRepository.findAllByPosterId(1L)).thenReturn(List.of());

        List<NoticeResponseDTO> result = noticeService.getNoticesByPosterId(1L);

        assertThat(result).isEmpty();
        verify(noticeRepository).findAllByPosterId(1L);
    }
}