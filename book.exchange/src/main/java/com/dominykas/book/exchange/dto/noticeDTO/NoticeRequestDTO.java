package com.dominykas.book.exchange.dto.noticeDTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class NoticeRequestDTO {

    private LocalDate timePosted;
    private String wishInReturn;
    private Long posterId;
    private Long publicationId;
}
