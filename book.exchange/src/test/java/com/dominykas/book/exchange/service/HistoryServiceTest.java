package com.dominykas.book.exchange.service;

import com.dominykas.book.exchange.dto.historyDTO.HistoryRequestDTO;
import com.dominykas.book.exchange.dto.historyDTO.HistoryResponseDTO;
import com.dominykas.book.exchange.entity.History;
import com.dominykas.book.exchange.entity.Notice;
import com.dominykas.book.exchange.entity.Publication;
import com.dominykas.book.exchange.entity.User;
import com.dominykas.book.exchange.entity.enums.ExchangeRequestStatus;
import com.dominykas.book.exchange.entity.enums.NoticeStatus;
import com.dominykas.book.exchange.repository.HistoryRepository;
import com.dominykas.book.exchange.repository.NoticeRepository;
import com.dominykas.book.exchange.repository.PublicationRepository;
import com.dominykas.book.exchange.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoryServiceTest {

    @Mock private HistoryRepository historyRepository;
    @Mock private PublicationRepository publicationRepository;
    @Mock private UserRepository userRepository;
    @Mock private NoticeRepository noticeRepository;

    @InjectMocks
    private HistoryService historyService;

    @Test
    void createHistory_ShouldCreateHistory() {
        HistoryRequestDTO request = new HistoryRequestDTO();
        request.setUserId(1L);
        request.setPosterUserId(2L);
        request.setNoticeId(3L);
        request.setGivenPublicationId(4L);
        request.setReceivedPublicationId(5L);
        request.setStatus(ExchangeRequestStatus.ACCEPTED);
        request.setTimeExchanged(LocalDate.now());

        User user = fullUser(1L);
        User poster = fullUser(2L);

        Publication given = publication(4L, "Given");
        Publication received = publication(5L, "Received");

        Notice notice = notice(3L, poster, received);

        History saved = history(10L, user, poster, notice, given, received);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(poster));
        when(noticeRepository.findById(3L)).thenReturn(Optional.of(notice));
        when(publicationRepository.findById(4L)).thenReturn(Optional.of(given));
        when(publicationRepository.findById(5L)).thenReturn(Optional.of(received));
        when(historyRepository.save(any(History.class))).thenReturn(saved);

        HistoryResponseDTO result = historyService.createHistory(request);

        assertThat(result).isNotNull();
        verify(historyRepository).save(any(History.class));
    }

    @Test
    void getHistoryById_ShouldReturnHistory() {
        User user = fullUser(1L);
        User poster = fullUser(2L);

        Publication given = publication(4L, "Given");
        Publication received = publication(5L, "Received");

        Notice notice = notice(3L, poster, received);

        History history = history(10L, user, poster, notice, given, received);

        when(historyRepository.findById(10L)).thenReturn(Optional.of(history));

        HistoryResponseDTO result = historyService.getHistoryById(10L);

        assertThat(result).isNotNull();
        verify(historyRepository).findById(10L);
    }

    @Test
    void getHistoryById_WhenMissing_ShouldThrowException() {
        when(historyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> historyService.getHistoryById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("History entry not found");
    }

    @Test
    void getHistoryByUserId_ShouldReturnList() {
        when(historyRepository.findAllByUserIdOrPosterUserIdOrderByTimeExchangedDescIdDesc(1L, 1L))
                .thenReturn(List.of());

        List<HistoryResponseDTO> result = historyService.getHistoryByUserId(1L);

        assertThat(result).isEmpty();
        verify(historyRepository).findAllByUserIdOrPosterUserIdOrderByTimeExchangedDescIdDesc(1L, 1L);
    }

    private User fullUser(Long id) {
        return User.builder()
                .id(id)
                .email("user" + id + "@gmail.com")
                .firstName("First" + id)
                .lastName("Last" + id)
                .build();
    }

    private Publication publication(Long id, String title) {
        Publication publication = new Publication();
        publication.setId(id);
        publication.setTitle(title);
        publication.setAuthor("Author " + id);
        publication.setLanguage("en");
        return publication;
    }

    private Notice notice(Long id, User poster, Publication publication) {
        Notice notice = new Notice();
        notice.setId(id);
        notice.setPoster(poster);
        notice.setPublication(publication);
        notice.setStatus(NoticeStatus.CLOSED);
        notice.setTimePosted(LocalDate.now());
        return notice;
    }

    private History history(
            Long id,
            User user,
            User poster,
            Notice notice,
            Publication given,
            Publication received
    ) {
        History history = new History();
        history.setId(id);
        history.setUser(user);
        history.setPosterUser(poster);
        history.setNotice(notice);
        history.setGivenPublication(given);
        history.setReceivedPublication(received);
        history.setStatus(ExchangeRequestStatus.ACCEPTED);
        history.setTimeExchanged(LocalDate.now());
        return history;
    }
}