package com.dominykas.book.exchange.dto.aiChatDTO;

import com.dominykas.book.exchange.dto.noticeDTO.NoticeResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiChatMessageResponseDTO {

    private Long id;
    private String role;
    private String text;
    private LocalDateTime createdAt;
    private List<NoticeResponseDTO> notices;
}