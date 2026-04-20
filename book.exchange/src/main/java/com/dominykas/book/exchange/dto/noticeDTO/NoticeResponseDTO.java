package com.dominykas.book.exchange.dto.noticeDTO;

import com.dominykas.book.exchange.dto.publicationDTO.PublicationResponseDTO;
import com.dominykas.book.exchange.dto.userDTO.UserResponseDTO;
import com.dominykas.book.exchange.entity.enums.NoticeStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class NoticeResponseDTO {
    private Long id;
    private LocalDate timePosted;
    private String wishInReturn;
    private UserResponseDTO poster;
    private PublicationResponseDTO publication;
    private NoticeStatus status;
}