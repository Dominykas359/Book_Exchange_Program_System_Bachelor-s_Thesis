package com.dominykas.book.exchange.dto.noticeDTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class NoticeFilterDTO {
    private Long posterId;
    private String title;
    private String author;
    private String language;
    private LocalDate releaseYearFrom;
    private LocalDate releaseYearTo;
    private Integer minPageCount;
    private Integer maxPageCount;
    private String cover;
    private Boolean colored;
    private LocalDate postedFrom;
    private LocalDate postedTo;
}