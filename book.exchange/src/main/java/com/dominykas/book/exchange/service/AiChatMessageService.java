package com.dominykas.book.exchange.service;

import com.dominykas.book.exchange.dto.aiChatDTO.AiChatMessageRequestDTO;
import com.dominykas.book.exchange.dto.aiChatDTO.AiChatMessageResponseDTO;
import com.dominykas.book.exchange.entity.AiChatMessage;
import com.dominykas.book.exchange.entity.Notice;
import com.dominykas.book.exchange.mapper.AiChatMessageMapper;
import com.dominykas.book.exchange.repository.AiChatMessageRepository;
import com.dominykas.book.exchange.repository.NoticeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiChatMessageService {

    private final AiChatMessageRepository aiChatMessageRepository;
    private final NoticeRepository noticeRepository;

    @Transactional
    public AiChatMessageResponseDTO createMessage(AiChatMessageRequestDTO aiChatMessageRequestDTO) {
        AiChatMessage aiChatMessage = new AiChatMessage();

        aiChatMessage.setUserId(aiChatMessageRequestDTO.getUserId());
        aiChatMessage.setRole(safe(aiChatMessageRequestDTO.getRole()));
        aiChatMessage.setText(safe(aiChatMessageRequestDTO.getText()));
        aiChatMessage.setCreatedAt(LocalDateTime.now());

        List<Long> noticeIds = aiChatMessageRequestDTO.getNoticeIds();
        if (noticeIds != null && !noticeIds.isEmpty()) {
            List<Notice> fetchedNotices = noticeRepository.findAllById(noticeIds);

            Map<Long, Notice> noticesById = fetchedNotices.stream()
                    .collect(Collectors.toMap(Notice::getId, Function.identity()));

            List<Notice> orderedNotices = noticeIds.stream()
                    .map(noticesById::get)
                    .filter(notice -> notice != null)
                    .toList();

            aiChatMessage.setNotices(orderedNotices);
        }

        aiChatMessage = aiChatMessageRepository.save(aiChatMessage);

        return AiChatMessageMapper.toDto(aiChatMessage);
    }

    public List<AiChatMessageResponseDTO> getMessagesByUserId(Long userId) {
        return aiChatMessageRepository.findAllByUserIdOrderByCreatedAtAscIdAsc(userId)
                .stream()
                .map(AiChatMessageMapper::toDto)
                .toList();
    }

    @Transactional
    public void clearHistory(Long userId) {
        aiChatMessageRepository.deleteAllByUserId(userId);
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}