package com.dominykas.book.exchange.dto.publicationDTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PublicationRequestDTO {

    private String title;
    private String author;
    private LocalDate releaseYear;
    private String language;
    private String description;
    private Integer pageCount;
    private String cover;
    private Boolean colored;
    private Integer editionNumber;
}
