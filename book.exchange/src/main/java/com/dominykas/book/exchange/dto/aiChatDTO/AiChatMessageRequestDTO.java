package com.dominykas.book.exchange.dto.aiChatDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiChatMessageRequestDTO {

    private Long userId;
    private String role;
    private String text;
    private List<Long> noticeIds;
}