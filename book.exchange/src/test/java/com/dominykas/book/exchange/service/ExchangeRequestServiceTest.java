package com.dominykas.book.exchange.service;

import com.dominykas.book.exchange.dto.exchangeRequestDTO.AcceptExchangeRequestDTO;
import com.dominykas.book.exchange.dto.exchangeRequestDTO.ExchangeRequestRequestDTO;
import com.dominykas.book.exchange.dto.exchangeRequestDTO.ExchangeRequestResponseDTO;
import com.dominykas.book.exchange.entity.ExchangeRequest;
import com.dominykas.book.exchange.entity.Notice;
import com.dominykas.book.exchange.entity.Publication;
import com.dominykas.book.exchange.entity.User;
import com.dominykas.book.exchange.entity.enums.ExchangeRequestStatus;
import com.dominykas.book.exchange.entity.enums.NoticeStatus;
import com.dominykas.book.exchange.repository.ExchangeRequestRepository;
import com.dominykas.book.exchange.repository.NoticeRepository;
import com.dominykas.book.exchange.repository.PublicationRepository;
import com.dominykas.book.exchange.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRequestServiceTest {

    @Mock private ExchangeRequestRepository exchangeRequestRepository;
    @Mock private UserRepository userRepository;
    @Mock private NoticeRepository noticeRepository;
    @Mock private PublicationRepository publicationRepository;
    @Mock private HistoryService historyService;

    @InjectMocks
    private ExchangeRequestService service;

    @Test
    void createExchangeRequest_ShouldCreatePendingRequest() {
        ExchangeRequestRequestDTO request = new ExchangeRequestRequestDTO();
        request.setUserId(1L);
        request.setRequestedFromUserId(2L);
        request.setNoticeId(3L);
        request.setGivenPublicationId(4L);
        request.setReceivedPublicationId(5L);
        request.setRequesterAddress(" Address ");

        User requester = fullUser(1L);
        User poster = fullUser(2L);

        Publication given = publication(4L);
        Publication received = publication(5L);

        Notice notice = notice(3L, poster, received, NoticeStatus.OPEN);

        ExchangeRequest saved = exchangeRequest(
                10L,
                requester,
                poster,
                notice,
                given,
                received,
                ExchangeRequestStatus.PENDING
        );
        saved.setRequesterAddress("Address");
        saved.setRequestedTime(LocalDate.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(userRepository.findById(2L)).thenReturn(Optional.of(poster));
        when(noticeRepository.findById(3L)).thenReturn(Optional.of(notice));
        when(publicationRepository.findById(4L)).thenReturn(Optional.of(given));
        when(publicationRepository.findById(5L)).thenReturn(Optional.of(received));
        when(exchangeRequestRepository.existsByUserIdAndNoticeIdAndStatus(1L, 3L, ExchangeRequestStatus.PENDING))
                .thenReturn(false);
        when(exchangeRequestRepository.save(any(ExchangeRequest.class))).thenReturn(saved);

        ExchangeRequestResponseDTO result = service.createExchangeRequest(request);

        assertThat(result).isNotNull();
        verify(exchangeRequestRepository).save(any(ExchangeRequest.class));
    }

    @Test
    void createExchangeRequest_WhenSameUser_ShouldThrowException() {
        ExchangeRequestRequestDTO request = new ExchangeRequestRequestDTO();
        request.setUserId(1L);
        request.setRequestedFromUserId(1L);
        request.setNoticeId(3L);
        request.setGivenPublicationId(4L);
        request.setReceivedPublicationId(5L);

        User user = fullUser(1L);
        Publication given = publication(4L);
        Publication received = publication(5L);
        Notice notice = notice(3L, user, received, NoticeStatus.OPEN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(noticeRepository.findById(3L)).thenReturn(Optional.of(notice));
        when(publicationRepository.findById(4L)).thenReturn(Optional.of(given));
        when(publicationRepository.findById(5L)).thenReturn(Optional.of(received));

        assertThatThrownBy(() -> service.createExchangeRequest(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User cannot send exchange request to themselves");
    }

    @Test
    void acceptExchangeRequest_ShouldAcceptAndCreateHistory() {
        User requester = fullUser(1L);
        User poster = fullUser(2L);

        Publication given = publication(4L);
        Publication received = publication(5L);

        Notice notice = notice(3L, poster, received, NoticeStatus.OPEN);

        ExchangeRequest exchangeRequest = exchangeRequest(
                10L,
                requester,
                poster,
                notice,
                given,
                received,
                ExchangeRequestStatus.PENDING
        );
        exchangeRequest.setRequesterAddress("Requester address");

        AcceptExchangeRequestDTO request = new AcceptExchangeRequestDTO();
        request.setRequestedFromUserAddress(" Poster address ");

        when(exchangeRequestRepository.findById(10L)).thenReturn(Optional.of(exchangeRequest));
        when(exchangeRequestRepository.save(exchangeRequest)).thenReturn(exchangeRequest);

        ExchangeRequestResponseDTO result = service.acceptExchangeRequest(10L, request);

        assertThat(result).isNotNull();
        assertThat(exchangeRequest.getStatus()).isEqualTo(ExchangeRequestStatus.ACCEPTED);
        assertThat(notice.getStatus()).isEqualTo(NoticeStatus.CLOSED);

        verify(noticeRepository).save(notice);
        verify(historyService).createHistory(any());
    }

    @Test
    void declineExchangeRequest_ShouldDeclineAndCreateHistory() {
        User requester = fullUser(1L);
        User poster = fullUser(2L);

        Publication given = publication(4L);
        Publication received = publication(5L);

        Notice notice = notice(3L, poster, received, NoticeStatus.OPEN);

        ExchangeRequest exchangeRequest = exchangeRequest(
                10L,
                requester,
                poster,
                notice,
                given,
                received,
                ExchangeRequestStatus.PENDING
        );
        exchangeRequest.setRequesterAddress("Requester address");

        when(exchangeRequestRepository.findById(10L)).thenReturn(Optional.of(exchangeRequest));
        when(exchangeRequestRepository.save(exchangeRequest)).thenReturn(exchangeRequest);

        ExchangeRequestResponseDTO result = service.declineExchangeRequest(10L);

        assertThat(result).isNotNull();
        assertThat(exchangeRequest.getStatus()).isEqualTo(ExchangeRequestStatus.DECLINED);

        verify(historyService).createHistory(any());
    }

    @Test
    void deleteExchangeRequest_ShouldDelete() {
        ExchangeRequest exchangeRequest = new ExchangeRequest();
        exchangeRequest.setId(10L);

        when(exchangeRequestRepository.findById(10L)).thenReturn(Optional.of(exchangeRequest));

        service.deleteExchangeRequest(10L);

        verify(exchangeRequestRepository).delete(exchangeRequest);
    }

    private User fullUser(Long id) {
        return User.builder()
                .id(id)
                .email("user" + id + "@gmail.com")
                .firstName("First" + id)
                .lastName("Last" + id)
                .build();
    }

    private Publication publication(Long id) {
        Publication publication = new Publication();
        publication.setId(id);
        publication.setTitle("Book " + id);
        publication.setAuthor("Author " + id);
        publication.setLanguage("en");
        return publication;
    }

    private Notice notice(Long id, User poster, Publication publication, NoticeStatus status) {
        Notice notice = new Notice();
        notice.setId(id);
        notice.setPoster(poster);
        notice.setPublication(publication);
        notice.setStatus(status);
        notice.setTimePosted(LocalDate.now());
        return notice;
    }

    private ExchangeRequest exchangeRequest(
            Long id,
            User requester,
            User poster,
            Notice notice,
            Publication given,
            Publication received,
            ExchangeRequestStatus status
    ) {
        ExchangeRequest exchangeRequest = new ExchangeRequest();
        exchangeRequest.setId(id);
        exchangeRequest.setUser(requester);
        exchangeRequest.setRequestedFromUser(poster);
        exchangeRequest.setNotice(notice);
        exchangeRequest.setGivenPublication(given);
        exchangeRequest.setReceivedPublication(received);
        exchangeRequest.setStatus(status);
        exchangeRequest.setRequestedTime(LocalDate.now());
        return exchangeRequest;
    }
}