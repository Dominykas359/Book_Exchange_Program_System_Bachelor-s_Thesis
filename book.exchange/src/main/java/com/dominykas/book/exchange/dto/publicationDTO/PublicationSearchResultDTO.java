package com.dominykas.book.exchange.dto.publicationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicationSearchResultDTO {
    private PublicationResponseDTO publication;
    private double score;
}