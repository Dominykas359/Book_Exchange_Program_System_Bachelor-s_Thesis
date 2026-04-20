package com.dominykas.book.exchange.service;

import com.dominykas.book.exchange.dto.exchangeRequestDTO.AcceptExchangeRequestDTO;
import com.dominykas.book.exchange.dto.exchangeRequestDTO.ExchangeRequestRequestDTO;
import com.dominykas.book.exchange.dto.exchangeRequestDTO.ExchangeRequestResponseDTO;
import com.dominykas.book.exchange.dto.historyDTO.HistoryRequestDTO;
import com.dominykas.book.exchange.entity.ExchangeRequest;
import com.dominykas.book.exchange.entity.Notice;
import com.dominykas.book.exchange.entity.Publication;
import com.dominykas.book.exchange.entity.User;
import com.dominykas.book.exchange.entity.enums.ExchangeRequestStatus;
import com.dominykas.book.exchange.entity.enums.NoticeStatus;
import com.dominykas.book.exchange.mapper.ExchangeRequestMapper;
import com.dominykas.book.exchange.repository.ExchangeRequestRepository;
import com.dominykas.book.exchange.repository.NoticeRepository;
import com.dominykas.book.exchange.repository.PublicationRepository;
import com.dominykas.book.exchange.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExchangeRequestService {

    private final ExchangeRequestRepository exchangeRequestRepository;
    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;
    private final PublicationRepository publicationRepository;
    private final HistoryService historyService;

    public ExchangeRequestResponseDTO createExchangeRequest(ExchangeRequestRequestDTO dto) {
        ExchangeRequest exchangeRequest = ExchangeRequestMapper.fromDto(dto);

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + dto.getUserId()));

        User requestedFromUser = userRepository.findById(dto.getRequestedFromUserId())
                .orElseThrow(() -> new RuntimeException("Requested from user not found: " + dto.getRequestedFromUserId()));

        Notice notice = noticeRepository.findById(dto.getNoticeId())
                .orElseThrow(() -> new RuntimeException("Notice not found: " + dto.getNoticeId()));

        Publication givenPublication = publicationRepository.findById(dto.getGivenPublicationId())
                .orElseThrow(() -> new RuntimeException("Publication not found: " + dto.getGivenPublicationId()));

        Publication receivedPublication = publicationRepository.findById(dto.getReceivedPublicationId())
                .orElseThrow(() -> new RuntimeException("Publication not found: " + dto.getReceivedPublicationId()));

        if (dto.getUserId().equals(dto.getRequestedFromUserId())) {
            throw new RuntimeException("User cannot send exchange request to themselves");
        }

        if (notice.getPoster() == null || notice.getPoster().getId() == null) {
            throw new RuntimeException("Notice poster is missing");
        }

        if (notice.getStatus() == NoticeStatus.CLOSED) {
            throw new RuntimeException("Cannot create exchange request for a closed notice");
        }

        if (notice.getPoster().getId().equals(dto.getUserId())) {
            throw new RuntimeException("You cannot create an exchange request for your own notice");
        }

        if (!notice.getPoster().getId().equals(dto.getRequestedFromUserId())) {
            throw new RuntimeException("Requested from user must match notice poster");
        }

        if (!notice.getPublication().getId().equals(receivedPublication.getId())) {
            throw new RuntimeException("Received publication must match notice publication");
        }

        if (dto.getRequesterAddress() == null || dto.getRequesterAddress().isBlank()) {
            throw new RuntimeException("Requester address is required");
        }

        boolean alreadyExists = exchangeRequestRepository.existsByUserIdAndNoticeIdAndStatus(
                dto.getUserId(),
                dto.getNoticeId(),
                ExchangeRequestStatus.PENDING
        );

        if (alreadyExists) {
            throw new RuntimeException("Pending exchange request already exists for this user and notice");
        }

        exchangeRequest.setUser(user);
        exchangeRequest.setRequestedFromUser(requestedFromUser);
        exchangeRequest.setNotice(notice);
        exchangeRequest.setGivenPublication(givenPublication);
        exchangeRequest.setReceivedPublication(receivedPublication);
        exchangeRequest.setRequestedTime(LocalDate.now());
        exchangeRequest.setRequesterAddress(dto.getRequesterAddress().trim());
        exchangeRequest.setRequestedFromUserAddress(null);
        exchangeRequest.setStatus(ExchangeRequestStatus.PENDING);

        ExchangeRequest saved = exchangeRequestRepository.save(exchangeRequest);
        return ExchangeRequestMapper.toDto(saved);
    }

    public ExchangeRequestResponseDTO getExchangeRequestById(Long id) {
        ExchangeRequest exchangeRequest = exchangeRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exchange request not found: " + id));

        return ExchangeRequestMapper.toDto(exchangeRequest);
    }

    public List<ExchangeRequestResponseDTO> getExchangeRequestsByUserId(Long userId) {
        return exchangeRequestRepository.findAllByUserIdOrRequestedFromUserIdOrderByRequestedTimeDescIdDesc(userId, userId)
                .stream()
                .map(ExchangeRequestMapper::toDto)
                .toList();
    }

    public List<ExchangeRequestResponseDTO> getExchangeRequestsByNoticeId(Long noticeId) {
        return exchangeRequestRepository.findAllByNoticeIdOrderByRequestedTimeDescIdDesc(noticeId)
                .stream()
                .map(ExchangeRequestMapper::toDto)
                .toList();
    }

    public List<ExchangeRequestResponseDTO> getExchangeRequestsByStatus(ExchangeRequestStatus status) {
        return exchangeRequestRepository.findAllByStatusOrderByRequestedTimeDescIdDesc(status)
                .stream()
                .map(ExchangeRequestMapper::toDto)
                .toList();
    }

    public ExchangeRequestResponseDTO acceptExchangeRequest(Long id, AcceptExchangeRequestDTO dto) {
        ExchangeRequest exchangeRequest = exchangeRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exchange request not found: " + id));

        if (exchangeRequest.getStatus() != ExchangeRequestStatus.PENDING) {
            throw new RuntimeException("Only pending exchange requests can be accepted");
        }

        if (dto.getRequestedFromUserAddress() == null || dto.getRequestedFromUserAddress().isBlank()) {
            throw new RuntimeException("Requested from user address is required");
        }

        exchangeRequest.setRequestedFromUserAddress(dto.getRequestedFromUserAddress().trim());
        exchangeRequest.setStatus(ExchangeRequestStatus.ACCEPTED);

        Notice notice = exchangeRequest.getNotice();

        if (notice == null) {
            throw new RuntimeException("Exchange request notice is missing");
        }

        notice.setStatus(NoticeStatus.CLOSED);
        noticeRepository.save(notice);

        ExchangeRequest saved = exchangeRequestRepository.save(exchangeRequest);

        HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO();
        historyRequestDTO.setUserId(saved.getUser().getId());
        historyRequestDTO.setPosterUserId(saved.getRequestedFromUser().getId());
        historyRequestDTO.setNoticeId(saved.getNotice().getId());
        historyRequestDTO.setTimeExchanged(LocalDate.now());
        historyRequestDTO.setGivenPublicationId(saved.getGivenPublication().getId());
        historyRequestDTO.setReceivedPublicationId(saved.getReceivedPublication().getId());
        historyRequestDTO.setStatus(saved.getStatus());
        historyRequestDTO.setRequesterAddress(saved.getRequesterAddress());
        historyRequestDTO.setRequestedFromUserAddress(saved.getRequestedFromUserAddress());

        historyService.createHistory(historyRequestDTO);

        return ExchangeRequestMapper.toDto(saved);
    }

    public ExchangeRequestResponseDTO declineExchangeRequest(Long id) {
        ExchangeRequest exchangeRequest = exchangeRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exchange request not found: " + id));

        if (exchangeRequest.getStatus() != ExchangeRequestStatus.PENDING) {
            throw new RuntimeException("Only pending exchange requests can be declined");
        }

        exchangeRequest.setStatus(ExchangeRequestStatus.DECLINED);
        ExchangeRequest saved = exchangeRequestRepository.save(exchangeRequest);

        HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO();
        historyRequestDTO.setUserId(saved.getUser().getId());
        historyRequestDTO.setPosterUserId(saved.getRequestedFromUser().getId());
        historyRequestDTO.setNoticeId(saved.getNotice().getId());
        historyRequestDTO.setTimeExchanged(LocalDate.now());
        historyRequestDTO.setGivenPublicationId(saved.getGivenPublication().getId());
        historyRequestDTO.setReceivedPublicationId(saved.getReceivedPublication().getId());
        historyRequestDTO.setStatus(saved.getStatus());
        historyRequestDTO.setRequesterAddress(saved.getRequesterAddress());
        historyRequestDTO.setRequestedFromUserAddress(saved.getRequestedFromUserAddress());

        historyService.createHistory(historyRequestDTO);

        return ExchangeRequestMapper.toDto(saved);
    }

    public void deleteExchangeRequest(Long id) {
        ExchangeRequest exchangeRequest = exchangeRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exchange request not found: " + id));

        exchangeRequestRepository.delete(exchangeRequest);
    }
}