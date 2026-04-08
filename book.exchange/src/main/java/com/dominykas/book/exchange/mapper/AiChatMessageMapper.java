package com.dominykas.book.exchange.mapper;

import com.dominykas.book.exchange.dto.aiChatDTO.AiChatMessageResponseDTO;
import com.dominykas.book.exchange.entity.AiChatMessage;

import java.util.List;

public class AiChatMessageMapper {

    public static AiChatMessageResponseDTO toDto(AiChatMessage aiChatMessage) {
        AiChatMessageResponseDTO dto = new AiChatMessageResponseDTO();

        dto.setId(aiChatMessage.getId());
        dto.setRole(aiChatMessage.getRole());
        dto.setText(aiChatMessage.getText());
        dto.setCreatedAt(aiChatMessage.getCreatedAt());

        if (aiChatMessage.getNotices() != null) {
            dto.setNotices(
                    aiChatMessage.getNotices()
                            .stream()
                            .map(NoticeMapper::toDto)
                            .toList()
            );
        } else {
            dto.setNotices(List.of());
        }

        return dto;
    }
}