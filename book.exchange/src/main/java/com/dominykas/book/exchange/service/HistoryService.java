package com.dominykas.book.exchange.service;

import com.dominykas.book.exchange.dto.historyDTO.HistoryRequestDTO;
import com.dominykas.book.exchange.dto.historyDTO.HistoryResponseDTO;
import com.dominykas.book.exchange.entity.History;
import com.dominykas.book.exchange.entity.Publication;
import com.dominykas.book.exchange.entity.User;
import com.dominykas.book.exchange.mapper.HistoryMapper;
import com.dominykas.book.exchange.repository.HistoryRepository;
import com.dominykas.book.exchange.repository.PublicationRepository;
import com.dominykas.book.exchange.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final PublicationRepository publicationRepository;
    private final UserRepository userRepository;

    public HistoryResponseDTO createHistory(HistoryRequestDTO dto) {
        History history = HistoryMapper.fromDto(dto);

        Publication givenPublication = publicationRepository.findById(dto.getGivenPublicationId())
                .orElseThrow(() -> new RuntimeException("Publication not found: " + dto.getGivenPublicationId()));

        Publication receivedPublication = publicationRepository.findById(dto.getReceivedPublicationId())
                .orElseThrow(() -> new RuntimeException("Publication not found: " + dto.getReceivedPublicationId()));

        history.setGivenPublication(givenPublication);
        history.setReceivedPublication(receivedPublication);

        if (history.getTimeExchanged() == null) {
            history.setTimeExchanged(LocalDate.now());
        }

        History saved = historyRepository.save(history);
        return HistoryMapper.toDto(saved);
    }

    public HistoryResponseDTO getHistoryById(Long id) {
        History history = historyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("History entry not found: " + id));

        return HistoryMapper.toDto(history);
    }

    public List<HistoryResponseDTO> getHistoryByUserId(Long userId) {
        return historyRepository.findAllByUserIdOrderByTimeExchangedDescIdDesc(userId)
                .stream()
                .map(HistoryMapper::toDto)
                .toList();
    }

    public List<HistoryResponseDTO> getMyHistory(Principal principal) {
        if (principal == null) {
            throw new RuntimeException("Unauthorized");
        }

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found: " + principal.getName()));

        return historyRepository.findAllByUserIdOrderByTimeExchangedDescIdDesc(user.getId())
                .stream()
                .map(HistoryMapper::toDto)
                .toList();
    }
}