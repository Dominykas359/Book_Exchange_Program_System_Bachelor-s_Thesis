package com.dominykas.book.exchange.service;

import com.dominykas.book.exchange.dto.exchangeRequestDTO.ExchangeRequestRequestDTO;
import com.dominykas.book.exchange.dto.exchangeRequestDTO.ExchangeRequestResponseDTO;
import com.dominykas.book.exchange.dto.historyDTO.HistoryRequestDTO;
import com.dominykas.book.exchange.entity.ExchangeRequest;
import com.dominykas.book.exchange.entity.enums.ExchangeRequestStatus;
import com.dominykas.book.exchange.entity.Notice;
import com.dominykas.book.exchange.entity.Publication;
import com.dominykas.book.exchange.entity.User;
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

        if (!notice.getPoster().getId().equals(dto.getRequestedFromUserId())) {
            throw new RuntimeException("Requested from user must match notice poster");
        }

        if (!notice.getPublication().getId().equals(receivedPublication.getId())) {
            throw new RuntimeException("Received publication must match notice publication");
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

    public ExchangeRequestResponseDTO acceptExchangeRequest(Long id) {
        ExchangeRequest exchangeRequest = exchangeRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exchange request not found: " + id));

        if (exchangeRequest.getStatus() != ExchangeRequestStatus.PENDING) {
            throw new RuntimeException("Only pending exchange requests can be accepted");
        }

        exchangeRequest.setStatus(ExchangeRequestStatus.ACCEPTED);
        ExchangeRequest saved = exchangeRequestRepository.save(exchangeRequest);

        HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO();
        historyRequestDTO.setUserId(saved.getUser().getId());
        historyRequestDTO.setPosterUserId(saved.getRequestedFromUser().getId());
        historyRequestDTO.setNoticeId(saved.getNotice().getId());
        historyRequestDTO.setTimeExchanged(LocalDate.now());
        historyRequestDTO.setGivenPublicationId(saved.getGivenPublication().getId());
        historyRequestDTO.setReceivedPublicationId(saved.getReceivedPublication().getId());
        historyRequestDTO.setStatus(saved.getStatus());

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

        historyService.createHistory(historyRequestDTO);

        return ExchangeRequestMapper.toDto(saved);
    }

    public void deleteExchangeRequest(Long id) {
        ExchangeRequest exchangeRequest = exchangeRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exchange request not found: " + id));

        exchangeRequestRepository.delete(exchangeRequest);
    }
}