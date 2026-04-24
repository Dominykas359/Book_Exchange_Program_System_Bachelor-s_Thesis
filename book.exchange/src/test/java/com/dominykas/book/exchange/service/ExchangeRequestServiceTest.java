package com.dominykas.book.exchange.service;

import com.dominykas.book.exchange.dto.exchangeRequestDTO.AcceptExchangeRequestDTO;
import com.dominykas.book.exchange.dto.exchangeRequestDTO.ExchangeRequestRequestDTO;
import com.dominykas.book.exchange.dto.exchangeRequestDTO.ExchangeRequestResponseDTO;
import com.dominykas.book.exchange.entity.*;
import com.dominykas.book.exchange.entity.enums.ExchangeRequestStatus;
import com.dominykas.book.exchange.entity.enums.NoticeStatus;
import com.dominykas.book.exchange.repository.*;
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

    // ===================== CREATE =====================

    @Test
    void createExchangeRequest_ShouldCreatePendingRequest() {
        ExchangeRequestRequestDTO request = baseRequest();

        User requester = fullUser(1L);
        User poster = fullUser(2L);

        Publication given = publication(4L);
        Publication received = publication(5L);

        Notice notice = notice(3L, poster, received, NoticeStatus.OPEN);

        ExchangeRequest saved = exchangeRequest(10L, requester, poster, notice, given, received, ExchangeRequestStatus.PENDING);

        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(userRepository.findById(2L)).thenReturn(Optional.of(poster));
        when(noticeRepository.findById(3L)).thenReturn(Optional.of(notice));
        when(publicationRepository.findById(4L)).thenReturn(Optional.of(given));
        when(publicationRepository.findById(5L)).thenReturn(Optional.of(received));
        when(exchangeRequestRepository.existsByUserIdAndNoticeIdAndStatus(any(), any(), any())).thenReturn(false);
        when(exchangeRequestRepository.save(any())).thenReturn(saved);

        ExchangeRequestResponseDTO result = service.createExchangeRequest(request);

        assertThat(result).isNotNull();
    }

    @Test
    void createExchangeRequest_WhenSameUser_ShouldThrow() {
        ExchangeRequestRequestDTO request = baseRequest();
        request.setRequestedFromUserId(1L);

        User user = fullUser(1L);
        Publication given = publication(4L);
        Publication received = publication(5L);
        Notice notice = notice(3L, user, received, NoticeStatus.OPEN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(noticeRepository.findById(3L)).thenReturn(Optional.of(notice));
        when(publicationRepository.findById(4L)).thenReturn(Optional.of(given));
        when(publicationRepository.findById(5L)).thenReturn(Optional.of(received));

        assertThatThrownBy(() -> service.createExchangeRequest(request))
                .hasMessageContaining("User cannot send exchange request to themselves");
    }

    @Test
    void createExchangeRequest_WhenNoticeClosed_ShouldThrow() {
        ExchangeRequestRequestDTO request = baseRequest();

        User requester = fullUser(1L);
        User poster = fullUser(2L);

        Publication given = publication(4L);
        Publication received = publication(5L);

        Notice notice = notice(3L, poster, received, NoticeStatus.CLOSED);

        mockCommon(requester, poster, notice, given, received);

        assertThatThrownBy(() -> service.createExchangeRequest(request))
                .hasMessageContaining("closed notice");
    }

    @Test
    void createExchangeRequest_WhenPosterMismatch_ShouldThrow() {
        ExchangeRequestRequestDTO request = baseRequest();
        request.setRequestedFromUserId(99L);

        User requester = fullUser(1L);
        User poster = fullUser(2L);
        User wrongUser = fullUser(99L);

        Publication given = publication(4L);
        Publication received = publication(5L);

        Notice notice = notice(3L, poster, received, NoticeStatus.OPEN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(userRepository.findById(99L)).thenReturn(Optional.of(wrongUser));
        when(noticeRepository.findById(3L)).thenReturn(Optional.of(notice));
        when(publicationRepository.findById(4L)).thenReturn(Optional.of(given));
        when(publicationRepository.findById(5L)).thenReturn(Optional.of(received));

        assertThatThrownBy(() -> service.createExchangeRequest(request))
                .hasMessageContaining("Requested from user must match notice poster");
    }

    @Test
    void createExchangeRequest_WhenPublicationMismatch_ShouldThrow() {
        ExchangeRequestRequestDTO request = baseRequest();

        User requester = fullUser(1L);
        User poster = fullUser(2L);

        Publication given = publication(4L);
        Publication received = publication(999L);

        Notice notice = notice(3L, poster, publication(5L), NoticeStatus.OPEN);

        mockCommon(requester, poster, notice, given, received);

        assertThatThrownBy(() -> service.createExchangeRequest(request))
                .hasMessageContaining("Received publication must match notice publication");
    }

    @Test
    void createExchangeRequest_WhenAddressBlank_ShouldThrow() {
        ExchangeRequestRequestDTO request = baseRequest();
        request.setRequesterAddress(" ");

        User requester = fullUser(1L);
        User poster = fullUser(2L);

        Publication given = publication(4L);
        Publication received = publication(5L);

        Notice notice = notice(3L, poster, received, NoticeStatus.OPEN);

        mockCommon(requester, poster, notice, given, received);

        assertThatThrownBy(() -> service.createExchangeRequest(request))
                .hasMessageContaining("Requester address is required");
    }

    @Test
    void createExchangeRequest_WhenAlreadyExists_ShouldThrow() {
        ExchangeRequestRequestDTO request = baseRequest();

        User requester = fullUser(1L);
        User poster = fullUser(2L);

        Publication given = publication(4L);
        Publication received = publication(5L);

        Notice notice = notice(3L, poster, received, NoticeStatus.OPEN);

        mockCommon(requester, poster, notice, given, received);

        when(exchangeRequestRepository.existsByUserIdAndNoticeIdAndStatus(any(), any(), any()))
                .thenReturn(true);

        assertThatThrownBy(() -> service.createExchangeRequest(request))
                .hasMessageContaining("already exists");
    }

    // ===================== ACCEPT =====================

    @Test
    void acceptExchangeRequest_WhenNotPending_ShouldThrow() {
        ExchangeRequest req = exchangeRequest(10L, fullUser(1L), fullUser(2L),
                notice(3L, fullUser(2L), publication(5L), NoticeStatus.OPEN),
                publication(4L), publication(5L), ExchangeRequestStatus.ACCEPTED);

        when(exchangeRequestRepository.findById(10L)).thenReturn(Optional.of(req));

        AcceptExchangeRequestDTO dto = new AcceptExchangeRequestDTO();
        dto.setRequestedFromUserAddress("addr");

        assertThatThrownBy(() -> service.acceptExchangeRequest(10L, dto))
                .hasMessageContaining("Only pending");
    }

    @Test
    void acceptExchangeRequest_WhenAddressBlank_ShouldThrow() {
        ExchangeRequest req = pendingRequest();

        when(exchangeRequestRepository.findById(10L)).thenReturn(Optional.of(req));

        AcceptExchangeRequestDTO dto = new AcceptExchangeRequestDTO();
        dto.setRequestedFromUserAddress(" ");

        assertThatThrownBy(() -> service.acceptExchangeRequest(10L, dto))
                .hasMessageContaining("address is required");
    }

    @Test
    void acceptExchangeRequest_WhenNoticeMissing_ShouldThrow() {
        ExchangeRequest req = pendingRequest();
        req.setNotice(null);

        when(exchangeRequestRepository.findById(10L)).thenReturn(Optional.of(req));

        AcceptExchangeRequestDTO dto = new AcceptExchangeRequestDTO();
        dto.setRequestedFromUserAddress("addr");

        assertThatThrownBy(() -> service.acceptExchangeRequest(10L, dto))
                .hasMessageContaining("notice is missing");
    }

    // ===================== DECLINE =====================

    @Test
    void declineExchangeRequest_WhenNotPending_ShouldThrow() {
        ExchangeRequest req = exchangeRequest(10L, fullUser(1L), fullUser(2L),
                notice(3L, fullUser(2L), publication(5L), NoticeStatus.OPEN),
                publication(4L), publication(5L), ExchangeRequestStatus.ACCEPTED);

        when(exchangeRequestRepository.findById(10L)).thenReturn(Optional.of(req));

        assertThatThrownBy(() -> service.declineExchangeRequest(10L))
                .hasMessageContaining("Only pending");
    }

    // ===================== HELPERS =====================

    private ExchangeRequestRequestDTO baseRequest() {
        ExchangeRequestRequestDTO request = new ExchangeRequestRequestDTO();
        request.setUserId(1L);
        request.setRequestedFromUserId(2L);
        request.setNoticeId(3L);
        request.setGivenPublicationId(4L);
        request.setReceivedPublicationId(5L);
        request.setRequesterAddress("Address");
        return request;
    }

    private void mockCommon(User requester, User poster, Notice notice,
                            Publication given, Publication received) {
        lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        lenient().when(userRepository.findById(2L)).thenReturn(Optional.of(poster));
        lenient().when(noticeRepository.findById(3L)).thenReturn(Optional.of(notice));
        lenient().when(publicationRepository.findById(4L)).thenReturn(Optional.of(given));
        lenient().when(publicationRepository.findById(5L)).thenReturn(Optional.of(received));
        lenient().when(exchangeRequestRepository.existsByUserIdAndNoticeIdAndStatus(any(), any(), any()))
                .thenReturn(false);
    }

    private ExchangeRequest pendingRequest() {
        return exchangeRequest(10L, fullUser(1L), fullUser(2L),
                notice(3L, fullUser(2L), publication(5L), NoticeStatus.OPEN),
                publication(4L), publication(5L), ExchangeRequestStatus.PENDING);
    }

    private User fullUser(Long id) {
        return User.builder().id(id).build();
    }

    private Publication publication(Long id) {
        Publication p = new Publication();
        p.setId(id);
        return p;
    }

    private Notice notice(Long id, User poster, Publication publication, NoticeStatus status) {
        Notice n = new Notice();
        n.setId(id);
        n.setPoster(poster);
        n.setPublication(publication);
        n.setStatus(status);
        n.setTimePosted(LocalDate.now());
        return n;
    }

    private ExchangeRequest exchangeRequest(Long id, User requester, User poster,
                                            Notice notice, Publication given,
                                            Publication received, ExchangeRequestStatus status) {
        ExchangeRequest e = new ExchangeRequest();
        e.setId(id);
        e.setUser(requester);
        e.setRequestedFromUser(poster);
        e.setNotice(notice);
        e.setGivenPublication(given);
        e.setReceivedPublication(received);
        e.setStatus(status);
        e.setRequestedTime(LocalDate.now());
        return e;
    }
}